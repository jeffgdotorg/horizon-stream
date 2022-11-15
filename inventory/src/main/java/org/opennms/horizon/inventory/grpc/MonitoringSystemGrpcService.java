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

package org.opennms.horizon.inventory.grpc;

import java.util.List;
import java.util.Optional;

import org.opennms.horizon.inventory.dto.MonitoringSystemDTO;
import org.opennms.horizon.inventory.dto.MonitoringSystemList;
import org.opennms.horizon.inventory.dto.MonitoringSystemServiceGrpc;
import org.opennms.horizon.inventory.service.MonitoringSystemService;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.Context;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MonitoringSystemGrpcService extends MonitoringSystemServiceGrpc.MonitoringSystemServiceImplBase {
    private final MonitoringSystemService service;
    private final TenantLookup tenantLookup;
    @Override
    public void listMonitoringSystem(Empty request, StreamObserver<MonitoringSystemList> responseObserver) {
        List<MonitoringSystemDTO> list = tenantLookup.lookupTenantId(Context.current())
            .map(service::findByTenantId)
            .orElseThrow();
        responseObserver.onNext(MonitoringSystemList.newBuilder().addAllList(list).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMonitoringSystemById(StringValue systemId, StreamObserver<MonitoringSystemDTO> responseObserver) {
        Optional<MonitoringSystemDTO> monitoringSystem = tenantLookup.lookupTenantId(Context.current())
            .map(tenant_id -> service.findBySystemId(systemId.getValue(), tenant_id))
            .orElseThrow();
        if(monitoringSystem.isPresent()) {
            responseObserver.onNext(monitoringSystem.get());
        } else {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage("Monitor system with system id: " + systemId + " doesn't exist")
                .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
        responseObserver.onCompleted();
    }


}