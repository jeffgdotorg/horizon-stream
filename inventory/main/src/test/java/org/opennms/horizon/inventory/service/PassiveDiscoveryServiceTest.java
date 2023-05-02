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

package org.opennms.horizon.inventory.service;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.opennms.horizon.inventory.dto.NodeCreateDTO;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.dto.PassiveDiscoveryUpsertDTO;
import org.opennms.horizon.inventory.dto.TagCreateDTO;
import org.opennms.horizon.inventory.dto.TagCreateListDTO;
import org.opennms.horizon.inventory.exception.EntityExistException;
import org.opennms.horizon.inventory.exception.InventoryRuntimeException;
import org.opennms.horizon.inventory.mapper.NodeMapper;
import org.opennms.horizon.inventory.mapper.discovery.PassiveDiscoveryMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.opennms.horizon.inventory.repository.MonitoringSystemRepository;
import org.opennms.horizon.inventory.repository.NodeRepository;
import org.opennms.horizon.inventory.repository.discovery.PassiveDiscoveryRepository;
import org.opennms.horizon.inventory.service.discovery.PassiveDiscoveryService;
import org.opennms.horizon.inventory.service.taskset.CollectorTaskSetService;
import org.opennms.horizon.inventory.service.taskset.MonitorTaskSetService;
import org.opennms.horizon.inventory.service.taskset.ScannerTaskSetService;
import org.opennms.horizon.inventory.service.taskset.publisher.TaskSetPublisher;
import org.opennms.horizon.shared.constants.GrpcConstants;
import org.opennms.taskset.contract.ScanType;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PassiveDiscoveryServiceTest {
    PassiveDiscoveryService passiveDiscoveryService;
    private PassiveDiscoveryRepository passiveDiscoveryRepository;
    private TagService tagService;
    private NodeRepository nodeRepository;
    private ScannerTaskSetService scannerTaskSetService;

    @BeforeEach
    void prepareTest() {
        PassiveDiscoveryMapper passiveDiscoveryMapper = Mappers.getMapper(PassiveDiscoveryMapper.class);
        passiveDiscoveryService = new PassiveDiscoveryService(passiveDiscoveryMapper,
            passiveDiscoveryRepository, tagService,nodeRepository,scannerTaskSetService);

    }

    @Test
    public void validateCommunityStrings(){

        // No exception should be thrown..
        PassiveDiscoveryUpsertDTO valid = PassiveDiscoveryUpsertDTO
            .newBuilder().addCommunities("1.2.3.4").build();
        passiveDiscoveryService.validateCommunityStrings(valid);

        Exception exception = assertThrows(InventoryRuntimeException.class, () -> {
            List<String> communities = new ArrayList<>();
            communities.add("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
            PassiveDiscoveryUpsertDTO tooLong = PassiveDiscoveryUpsertDTO
                .newBuilder().addAllCommunities(communities)
                .build();
            passiveDiscoveryService.validateCommunityStrings(tooLong);
        });
        assertTrue(exception.getMessage().equals("Snmp communities string is too long"));

        Exception exception2 = assertThrows(InventoryRuntimeException.class, () -> {
            List<String> communities = new ArrayList<>();
            communities.add("ÿ");
            PassiveDiscoveryUpsertDTO invalidChars = PassiveDiscoveryUpsertDTO
                .newBuilder().addAllCommunities(communities)
                .build();
            passiveDiscoveryService.validateCommunityStrings(invalidChars);
        });
        assertTrue(exception2.getMessage().equals("All characters must be 7bit ascii"));
    }
}
