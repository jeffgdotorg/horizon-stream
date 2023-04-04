/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.horizon.inventory.grpc.node;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.Context;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.dto.MonitoredStateQuery;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.dto.NodeIdQuery;
import org.opennms.horizon.inventory.dto.NodeLabelSearchQuery;
import org.opennms.horizon.inventory.dto.NodeList;
import org.opennms.horizon.inventory.dto.NodeServiceGrpc;
import org.opennms.horizon.inventory.dto.TagNameQuery;
import org.opennms.horizon.inventory.grpc.TenantLookup;
import org.opennms.horizon.inventory.service.IpInterfaceService;
import org.opennms.horizon.inventory.service.node.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NodeGrpcService extends NodeServiceGrpc.NodeServiceImplBase {
    public static final String DIDNT_MATCH_NODE_ID_MSG = "Didn't find a valid node id with the given query";
    public static final String INVALID_REQUEST_LOCATION_AND_IP_NOT_EMPTY_MSG = "Invalid Request Query, location/ipAddress can't be empty";
    public static final String TENANT_ID_IS_MISSING_MSG = "Tenant ID is missing";
    public static final String IP_ADDRESS_ALREADY_EXISTS_FOR_LOCATION_MSG = "IP Address already exists for location";
    public static final String EMPTY_TENANT_ID_MSG = "Tenant Id can't be empty";
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(NodeGrpcService.class);

    @Setter
    private Logger LOG = DEFAULT_LOGGER;

    private final NodeService nodeService;
    private final IpInterfaceService ipInterfaceService;
    private final TenantLookup tenantLookup;

    @Override
    public void listNodes(Empty request, StreamObserver<NodeList> responseObserver) {
        List<NodeDTO> list = tenantLookup.lookupTenantId(Context.current())
            .map(nodeService::findByTenantId).orElseThrow();
        responseObserver.onNext(NodeList.newBuilder().addAllNodes(list).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listNodesByMonitoredState(MonitoredStateQuery request, StreamObserver<NodeList> responseObserver) {
        try {
            List<NodeDTO> list = tenantLookup.lookupTenantId(Context.current())
                .map((Function<String, List<NodeDTO>>) tenantId ->
                    nodeService.findByMonitoredState(tenantId, request.getMonitoredState())).orElseThrow();
            responseObserver.onNext(NodeList.newBuilder().addAllNodes(list).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getNodeById(Int64Value request, StreamObserver<NodeDTO> responseObserver) {
        Optional<NodeDTO> node = tenantLookup.lookupTenantId(Context.current())
            .map(tenantId -> nodeService.getByIdAndTenantId(request.getValue(), tenantId))
            .orElseThrow();
        node.ifPresentOrElse(nodeDTO -> {
            responseObserver.onNext(nodeDTO);
            responseObserver.onCompleted();
        }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatusNotExits(request.getValue()))));
    }

    @Override
    public void getNodeIdFromQuery(NodeIdQuery request,
                                   StreamObserver<Int64Value> responseObserver) {

        Optional<String> tenantIdOptional = tenantLookup.lookupTenantId(Context.current());

        if (tenantIdOptional.isEmpty()) {
            Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage(EMPTY_TENANT_ID_MSG)
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            return;
        }

        String tenantId = tenantIdOptional.get();
        String location = request.getLocation();
        String ipAddress = request.getIpAddress();
        if (Strings.isNullOrEmpty(location) || Strings.isNullOrEmpty(ipAddress)) {
            Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage(INVALID_REQUEST_LOCATION_AND_IP_NOT_EMPTY_MSG)
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            return;
        }

        Optional<IpInterfaceDTO> optional = ipInterfaceService.findByIpAddressAndLocationAndTenantId(ipAddress, location, tenantId);

        if (optional.isEmpty()) {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage(DIDNT_MATCH_NODE_ID_MSG)
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            return;
        }

        var ipInterface = optional.get();
        long nodeId = ipInterface.getNodeId();
        var nodeIdProto = Int64Value.newBuilder().setValue(nodeId).build();
        responseObserver.onNext(nodeIdProto);
        responseObserver.onCompleted();
    }

    @Override
    public void listNodesByNodeLabel(NodeLabelSearchQuery request, StreamObserver<NodeList> responseObserver) {
        Optional<String> tenantIdOptional = tenantLookup.lookupTenantId(Context.current());

        tenantIdOptional.ifPresentOrElse(tenantId -> {
            try {
                List<NodeDTO> nodes = nodeService.listNodesByNodeLabelSearch(tenantId, request.getSearchTerm());
                responseObserver.onNext(NodeList.newBuilder().addAllNodes(nodes).build());
                responseObserver.onCompleted();
            } catch (Exception e) {

                Status status = Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage(e.getMessage())
                    .build();
                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            }
        }, () -> {

            Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage("Tenant Id can't be empty")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        });
    }

    @Override
    public void listNodesByTags(TagNameQuery request, StreamObserver<NodeList> responseObserver) {
        Optional<String> tenantIdOptional = tenantLookup.lookupTenantId(Context.current());

        tenantIdOptional.ifPresentOrElse(tenantId -> {
            try {
                List<NodeDTO> nodes = nodeService.listNodesByTags(tenantId, request.getTagsList());
                responseObserver.onNext(NodeList.newBuilder().addAllNodes(nodes).build());
                responseObserver.onCompleted();
            } catch (Exception e) {

                Status status = Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage(e.getMessage())
                    .build();
                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            }
        }, () -> {

            Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage("Tenant Id can't be empty")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        });
    }

    @Override
    public void deleteNode(Int64Value request, StreamObserver<BoolValue> responseObserver) {
        // TBD888: why lookup the node, then delete it by ID?  How about just calling deleteNode() and skip the getByIdAndTenantId?
        Optional<NodeDTO> nodeOpt = tenantLookup.lookupTenantId(Context.current())
            .map(tenantId -> nodeService.getByIdAndTenantId(request.getValue(), tenantId))
            .orElseThrow();

        if (nodeOpt.isPresent()) {
            try {
                nodeService.deleteNode(request.getValue());
                responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                LOG.error("Error while deleting node with ID {}", request.getValue(), e);
                Status status = Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage("Error while deleting node with ID " + request.getValue()).build();
                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            }
        } else {
            responseObserver.onError(StatusProto.toStatusRuntimeException(createStatusNotExits(request.getValue())));
        }
    }

    @Override
    public void getIpInterfaceById(Int64Value request, StreamObserver<IpInterfaceDTO> responseObserver) {
        var ipInterface = tenantLookup.lookupTenantId(Context.current())
            .map(tenantId -> ipInterfaceService.getByIdAndTenantId(request.getValue(), tenantId))
            .orElseThrow();
        ipInterface.ifPresentOrElse(ipInterfaceDTO -> {
            responseObserver.onNext(ipInterfaceDTO);
            responseObserver.onCompleted();
        }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatusNotExits(request.getValue()))));

    }

    @Override
    public void getIpInterfaceFromQuery(NodeIdQuery request, StreamObserver<IpInterfaceDTO> responseObserver) {
        tenantLookup.lookupTenantId(Context.current()).ifPresentOrElse(tenantId -> {
            ipInterfaceService.findByIpAddressAndLocationAndTenantId(request.getIpAddress(), request.getLocation(), tenantId).ifPresentOrElse(ipInterface -> {
                responseObserver.onNext(ipInterface);
                responseObserver.onCompleted();
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage(String.format("IpInterface with IP: %s doesn't exist.", request.getIpAddress())).build())));
        }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createTenantIdMissingStatus())));
    }

    private Status createTenantIdMissingStatus() {
        return Status.newBuilder().setCode(Code.INVALID_ARGUMENT_VALUE).setMessage(TENANT_ID_IS_MISSING_MSG).build();
    }

    private Status createStatusNotExits(long id) {
        return Status.newBuilder()
            .setCode(Code.NOT_FOUND_VALUE)
            .setMessage(String.format("Node with id: %s doesn't exist.", id)).build();

    }
}