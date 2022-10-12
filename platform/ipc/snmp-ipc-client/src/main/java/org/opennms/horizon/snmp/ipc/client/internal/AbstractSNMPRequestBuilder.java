/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.snmp.ipc.client.internal;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;
import org.opennms.horizon.grpc.snmp.contract.SnmpGetRequest;
import org.opennms.horizon.grpc.snmp.contract.SnmpMultiResponse;
import org.opennms.horizon.grpc.snmp.contract.SnmpRequest;
import org.opennms.horizon.grpc.snmp.contract.SnmpResponse;
import org.opennms.horizon.grpc.snmp.contract.SnmpValue.ValueCase;
import org.opennms.horizon.grpc.snmp.contract.SnmpValueType;
import org.opennms.horizon.grpc.snmp.contract.SnmpWalkRequest;
import org.opennms.horizon.shared.snmp.SnmpInstId;
import org.opennms.horizon.shared.snmp.SnmpObjId;
import org.opennms.horizon.shared.snmp.SnmpResult;
import org.opennms.horizon.shared.snmp.SnmpValue;
import org.opennms.horizon.shared.snmp.SnmpValueFactory;
import org.opennms.horizon.shared.snmp.SnmpAgentConfig;
import org.opennms.horizon.shared.snmp.proxy.SNMPRequestBuilder;

public abstract class AbstractSNMPRequestBuilder<T> implements SNMPRequestBuilder<T> {

    private final LocationAwareSnmpClientRpcImpl client;
    private final SnmpAgentConfig agent;
    private final SnmpValueFactory valueFactory;
    private List<SnmpGetRequest> gets;
    private List<SnmpWalkRequest> walks;
    private String location;
    private String systemId;
    private String description;
    private Long timeToLiveInMilliseconds = null;

    public AbstractSNMPRequestBuilder(LocationAwareSnmpClientRpcImpl client, SnmpAgentConfig agent,
        SnmpValueFactory valueFactory, List<SnmpGetRequest> gets, List<SnmpWalkRequest> walks) {
        this.client = Objects.requireNonNull(client);
        this.agent = Objects.requireNonNull(agent);
        this.valueFactory = Objects.requireNonNull(valueFactory);
        this.gets = Objects.requireNonNull(gets);
        this.walks = Objects.requireNonNull(walks);
    }

    @Override
    public SNMPRequestBuilder<T> withLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public SNMPRequestBuilder<T> withSystemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    @Override
    public SNMPRequestBuilder<T> withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public SNMPRequestBuilder<T> withTimeToLive(Long ttlInMs) {
        timeToLiveInMilliseconds = ttlInMs;
        return this;
    }

    @Override
    public SNMPRequestBuilder<T> withTimeToLive(long duration, TimeUnit unit) {
        timeToLiveInMilliseconds = unit.toMillis(duration);
        return this;
    }

    @Override
    public CompletableFuture<T> execute() {
        SnmpRequest snmpRequestDTO = SnmpRequest.newBuilder()
            //.setAgent(agent)
            .setDescription(description)
            .addAllGets(gets)
            .addAllWalks(walks)
            .build();

        // TTL specified in agent configuration overwrites any previous ttls specified.
        if (agent.getTTL() != null) {
            timeToLiveInMilliseconds = agent.getTTL();
        }
        //snmpRequestDTO.setTimeToLive(timeToLiveInMilliseconds);
        //snmpRequestDTO.addTracingInfo(RpcRequest.TAG_IP_ADDRESS, InetAddressUtils.toIpAddrString(agent.getAddress()));
        //if (description != null) {
        //    snmpRequestDTO.addTracingInfo(RpcRequest.TAG_DESCRIPTION, description);
        //}
        return client.execute(location, systemId, snmpRequestDTO)
            // Different types of requests can process the responses differently
            .thenApply(this::processResponse);
    }

    protected final SnmpValue mapValue(org.opennms.horizon.grpc.snmp.contract.SnmpValue value) {
        SnmpValueType type = SnmpValueType.forNumber(value.getTypeValue());
        switch (type) {
            case INT32:
                return valueFactory.getInt32(Long.valueOf(value.getSint64()).intValue());
            case OCTET_STRING:
                return valueFactory.getOctetString(value.getBytes().toByteArray());
            case NULL:
                return valueFactory.getNull();
            case OBJECT_IDENTIFIER:
                return valueFactory.getObjectId(SnmpObjId.get(value.getString()));
            case IPADDRESS:
                try {
                    InetAddress address = InetAddress.getByAddress(value.getBytes().toByteArray());
                    return valueFactory.getIpAddress(address);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            case COUNTER32:
                return valueFactory.getCounter32(value.getUint64());
            case GAUGE32:
                return valueFactory.getGauge32(value.getUint64());
            case TIMETICKS:
                return valueFactory.getTimeTicks(value.getUint64());
            case OPAQUE:
                return valueFactory.getOpaque(value.getBytes().toByteArray());
            case COUNTER64:
                BigInteger integer = new BigInteger(value.getBytes().toByteArray());
                return valueFactory.getCounter64(integer);
            case NO_SUCH_OBJECT:
                return valueFactory.getValue(SnmpValueType.NO_SUCH_OBJECT_VALUE, new byte[0]);
            case NO_SUCH_INSTANCE:
                return valueFactory.getValue(SnmpValueType.NO_SUCH_INSTANCE_VALUE, new byte[0]);
            case END_OF_MIB:
                return valueFactory.getValue(SnmpValueType.END_OF_MIB_VALUE, new byte[0]);
        }
        throw new IllegalArgumentException("Unsupported value type");
    }

    protected final List<SnmpResult> mapResults(SnmpResponse res) {
        return res.getResultsList().stream()
            .map(result -> new SnmpResult(
                SnmpObjId.get(result.getBase()),
                new SnmpInstId(result.getInstance()),
                mapValue(result.getValue())
            ))
            .collect(Collectors.toList());
    }

    protected abstract T processResponse(SnmpMultiResponse response);
}
