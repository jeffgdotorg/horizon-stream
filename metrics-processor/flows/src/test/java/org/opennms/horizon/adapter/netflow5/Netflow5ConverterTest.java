/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.horizon.adapter.netflow5;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.opennms.horizon.shared.flows.BufferUtils.slice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.opennms.horizon.flows.adapter.common.NetflowMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opennms.horizon.flows.processing.enrichment.Flow;
import org.opennms.horizon.shared.flows.Direction;
import org.opennms.horizon.shared.flows.FlowMessage;
import org.opennms.horizon.shared.flows.NetflowVersion;
import org.opennms.horizon.shared.flows.SamplingAlgorithm;
import org.opennms.horizon.shared.flows.exceptions.InvalidPacketException;
import org.opennms.horizon.shared.flows.proto.Header;
import org.opennms.horizon.shared.flows.proto.Packet;
import org.opennms.horizon.shared.flows.transport.Netflow5MessageBuilder;

public class Netflow5ConverterTest {

    @Test
    public void canParseNetflow5Flows() {
        // Generate flows from existing packet payloads
        List<Flow> flows = getFlowsForPayloadsInSession("/flows/netflow5.dat");
        MatcherAssert.assertThat(flows, hasSize(2));

        final Flow flow = flows.get(0);
        MatcherAssert.assertThat(flow.getNetflowVersion(), is((NetflowVersion.V5)));
        MatcherAssert.assertThat(flow.getFlowRecords(), is(2));
        MatcherAssert.assertThat(flow.getFlowSeqNum(), is(0L));
        MatcherAssert.assertThat(flow.getEngineId(), is(0));
        MatcherAssert.assertThat(flow.getEngineType(), is(0));
        MatcherAssert.assertThat(flow.getSamplingInterval(), is(0.0));
        MatcherAssert.assertThat(flow.getSamplingAlgorithm(), is(SamplingAlgorithm.UNASSIGNED));
        MatcherAssert.assertThat(flow.getSrcAddr(), equalTo("10.0.2.2"));
        MatcherAssert.assertThat(flow.getSrcAddrHostname(), equalTo(Optional.empty()));
        MatcherAssert.assertThat(flow.getSrcPort(), equalTo(54435));
        MatcherAssert.assertThat(flow.getSrcMaskLen(), equalTo(0));
        MatcherAssert.assertThat(flow.getDstAddr(), equalTo("10.0.2.15"));
        MatcherAssert.assertThat(flow.getDstAddrHostname(), equalTo(Optional.empty()));
        MatcherAssert.assertThat(flow.getDstPort(), equalTo(22));
        MatcherAssert.assertThat(flow.getDstMaskLen(), equalTo(0));
        MatcherAssert.assertThat(flow.getTcpFlags(), equalTo(16));
        MatcherAssert.assertThat(flow.getProtocol(), equalTo(6)); // TCP
        MatcherAssert.assertThat(flow.getBytes(), equalTo(230L));
        MatcherAssert.assertThat(flow.getInputSnmp(), equalTo(0));
        MatcherAssert.assertThat(flow.getOutputSnmp(), equalTo(0));
        MatcherAssert.assertThat(flow.getFirstSwitched(), equalTo(Instant.ofEpochMilli(1430608661859L)));
        MatcherAssert.assertThat(flow.getLastSwitched(), equalTo(Instant.ofEpochMilli(1434870077556L)));
        MatcherAssert.assertThat(flow.getDeltaSwitched(), equalTo(Instant.ofEpochMilli(1430608661859L)));
        MatcherAssert.assertThat(flow.getPackets(), equalTo(5L));
        MatcherAssert.assertThat(flow.getDirection(), equalTo(Direction.INGRESS));
        MatcherAssert.assertThat(flow.getNextHop(), equalTo("0.0.0.0"));
        MatcherAssert.assertThat(flow.getNextHopHostname(), equalTo(Optional.empty()));
        MatcherAssert.assertThat(flow.getVlan(), nullValue());
    }

    private List<Flow> getFlowsForPayloadsInSession(String... resources) {
        final List<byte[]> payloads = new ArrayList<>(resources.length);
        for (String resource : resources) {
            URL resourceURL = getClass().getResource(resource);
            try {
                payloads.add(Files.readAllBytes(Paths.get(resourceURL.toURI())));
            } catch (IOException|URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return getFlowsForPayloadsInSession(payloads);
    }

    private List<Flow> getFlowsForPayloadsInSession(List<byte[]> payloads) {
        final List<Flow> flows = new ArrayList<>();
        for (byte[] payload : payloads) {
            final ByteBuf buffer = Unpooled.wrappedBuffer(payload);
            final Header header;
            try {
                header = new Header(slice(buffer, Header.SIZE));
                final Packet packet = new Packet(header, buffer);
                packet.getRecords().forEach(rec -> {
                    final FlowMessage flowMessage = new Netflow5MessageBuilder().buildMessage(rec, (address) -> Optional.empty()).build();
                    flows.add(new NetflowMessage(flowMessage, Instant.now()));
                });
            } catch (InvalidPacketException e) {
                throw new RuntimeException(e);
            }
        }
        return flows;
    }
}
