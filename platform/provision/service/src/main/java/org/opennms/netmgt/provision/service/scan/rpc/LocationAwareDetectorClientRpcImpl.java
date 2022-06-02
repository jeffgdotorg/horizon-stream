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

package org.opennms.netmgt.provision.service.scan.rpc;

//import org.opennms.core.rpc.api.RpcClient;
//import org.opennms.core.rpc.api.RpcClientFactory;
//import org.opennms.core.rpc.utils.mate.EntityScopeProvider;
import org.opennms.horizon.ipc.rpc.api.RpcClient;
import org.opennms.horizon.ipc.rpc.api.RpcClientFactory;
import org.opennms.netmgt.provision.DetectorRequestBuilder;
import org.opennms.netmgt.provision.LocationAwareDetectorClient;
//import org.opennms.netmgt.provision.detector.registry.api.ServiceDetectorRegistry;
import org.opennms.netmgt.provision.ServiceDetectorRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationAwareDetectorClientRpcImpl implements LocationAwareDetectorClient, InitializingBean {

    private ServiceDetectorRegistry registry;

    private DetectorClientRpcModule detectorClientRpcModule;

    private RpcClientFactory rpcClientFactory;

    private EntityScopeProvider entityScopeProvider;

    private RpcClient<DetectorRequestDTO, DetectorResponseDTO> delegate;

    @Override
    public void afterPropertiesSet() {
        delegate = rpcClientFactory.getClient(detectorClientRpcModule);
    }

    @Override
    public DetectorRequestBuilder detect() {
        return new DetectorRequestBuilderImpl(this);
    }

    protected RpcClient<DetectorRequestDTO, DetectorResponseDTO> getDelegate() {
        return delegate;
    }

    public void setRegistry(ServiceDetectorRegistry registry) {
        this.registry = registry;
    }

    public ServiceDetectorRegistry getRegistry() {
        return registry;
    }

    public EntityScopeProvider getEntityScopeProvider() {
        return this.entityScopeProvider;
    }

    public void setEntityScopeProvider(final EntityScopeProvider entityScopeProvider) {
        this.entityScopeProvider = entityScopeProvider;
    }
}
