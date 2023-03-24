/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
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

package org.opennms.horizon.alertservice.grpc;

import java.io.IOException;
import java.util.List;

import org.opennms.horizon.alertservice.db.tenant.TenantLookup;
import org.opennms.horizon.alertservice.service.MonitorPolicyService;
import org.opennms.horizon.shared.alert.policy.MonitorPolicyList;
import org.opennms.horizon.shared.alert.policy.MonitorPolicyProto;
import org.opennms.horizon.shared.alert.policy.MonitorPolicyServiceGrpc;
import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.Context;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitorPolicyGrpc extends MonitorPolicyServiceGrpc.MonitorPolicyServiceImplBase {
    private final MonitorPolicyService service;
    private final TenantLookup tenantLookup;

    @Override
    public void createPolicy(MonitorPolicyProto request, StreamObserver<MonitorPolicyProto> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenentId -> {
                try {
                    MonitorPolicyProto created = service.createPolicy(request, tenentId);
                    responseObserver.onNext(created);
                    responseObserver.onCompleted();
                } catch (IOException e) {
                    log.error("Error while creating policy {}", request, e);
                    responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INTERNAL_VALUE, "Error while creating policy: " + e.getMessage())));
                }
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(badTenant())));
    }

    @Override
    public void listPolicies(Empty request, StreamObserver<MonitorPolicyList> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                List<MonitorPolicyProto> list = service.listAll(tenantId);
                responseObserver.onNext(MonitorPolicyList.newBuilder().addAllPolicies(list).build());
                responseObserver.onCompleted();
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(badTenant())));
    }

    @Override
    public void getPolicyById(Int64Value request, StreamObserver<MonitorPolicyProto> responseObserver) {
        tenantLookup.lookupTenantId(Context.current())
            .ifPresentOrElse(tenantId -> {
                try {
                    service.findById(request.getValue(), tenantId)
                        .ifPresentOrElse(policy -> {
                            responseObserver.onNext(policy);
                            responseObserver.onCompleted();
                        }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.NOT_FOUND_VALUE,
                            "Policy with ID (" + request.getValue() + ") doesn't exist"))));
                } catch (IOException e) {
                    log.error("Error while find the policy by id {}", request.getValue(), e);
                    responseObserver.onError(StatusProto.toStatusRuntimeException(createStatus(Code.INTERNAL_VALUE, "Error happened while find policy by ID: " + request.getValue())));
                }
            }, () -> responseObserver.onError(StatusProto.toStatusRuntimeException(badTenant())));
    }

    private Status badTenant() {
        return createStatus(Code.INVALID_ARGUMENT_VALUE, "Tenant Id can't be empty");
    }

    private Status createStatus(int code, String msg) {
        return Status.newBuilder()
            .setCode(code)
            .setMessage(msg)
            .build();
    }
}
