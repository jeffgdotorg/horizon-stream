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

package org.opennms.horizon.inventory.service.node;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.inventory.dto.MonitoredState;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.mapper.node.NodeMapper;
import org.opennms.horizon.inventory.model.Tag;
import org.opennms.horizon.inventory.model.node.Node;
import org.opennms.horizon.inventory.repository.node.NodeRepository;
import org.opennms.horizon.inventory.service.taskset.CollectorTaskSetService;
import org.opennms.horizon.inventory.service.taskset.MonitorTaskSetService;
import org.opennms.horizon.inventory.service.taskset.ScannerTaskSetService;
import org.opennms.horizon.inventory.service.taskset.publisher.TaskSetPublisher;
import org.opennms.taskset.contract.MonitorType;
import org.opennms.taskset.contract.TaskDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeService {
    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("delete-node-task-publish-%d")
        .build();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory);
    private final NodeRepository nodeRepository;
    private final CollectorTaskSetService collectorTaskSetService;
    private final MonitorTaskSetService monitorTaskSetService;
    private final ScannerTaskSetService scannerTaskSetService;
    private final TaskSetPublisher taskSetPublisher;
    private final NodeMapper mapper;

    @Transactional(readOnly = true)
    public List<NodeDTO> findByTenantId(String tenantId) {
        List<Node> all = nodeRepository.findByTenantId(tenantId);
        return mapper.modelToDto(all);
    }

    @Transactional(readOnly = true)
    public Optional<NodeDTO> getByIdAndTenantId(long id, String tenantId) {
        return nodeRepository.findByIdAndTenantId(id, tenantId)
            .map((Function<Node, NodeDTO>) mapper::modelToDto);
    }

    @Transactional(readOnly = true)
    public List<NodeDTO> findByMonitoredState(String tenantId, MonitoredState monitoredState) {
        List<Node> all = nodeRepository.findByTenantIdAndMonitoredStateEquals(tenantId, monitoredState);
        return mapper.modelToDto(all);
    }

    @Transactional(readOnly = true)
    public Map<String, List<NodeDTO>> listNodeByIds(List<Long> ids, String tenantId) {
        List<Node> nodeList = nodeRepository.findByIdInAndTenantId(ids, tenantId);
        if (nodeList.isEmpty()) {
            return new HashMap<>();
        }
        return nodeList.stream().collect(Collectors.groupingBy(node -> node.getMonitoringLocation().getLocation(),
            Collectors.mapping((Function<Node, NodeDTO>) mapper::modelToDto, Collectors.toList())));
    }

    @Transactional
    public void deleteNode(long id) {
        Optional<Node> optionalNode = nodeRepository.findById(id);
        if (optionalNode.isEmpty()) {
            log.warn("Node with ID {} doesn't exist", id);
            throw new IllegalArgumentException("Node with ID : " + id + "doesn't exist");
        } else {
            var node = optionalNode.get();
            var tenantId = node.getTenantId();
            var location = node.getMonitoringLocation().getLocation();
            var tasks = getTasksForNode(node);
            removeAssociatedTags(node);
            nodeRepository.deleteById(id);
            executorService.execute(() -> taskSetPublisher.publishTaskDeletion(tenantId, location, tasks));
        }
    }

    public List<TaskDefinition> getTasksForNode(Node node) {
        var tasks = new ArrayList<TaskDefinition>();
        scannerTaskSetService.getNodeScanTasks(node).ifPresent(tasks::add);
        node.getIpInterfaces().forEach(ipInterface -> {
            ipInterface.getMonitoredServices().forEach((ms) -> {
                String serviceName = ms.getMonitoredServiceType().getServiceName();
                var monitorType = MonitorType.valueOf(serviceName);
                var monitorTask = monitorTaskSetService.getMonitorTask(monitorType, ipInterface, node.getId(), null);
                Optional.ofNullable(monitorTask).ifPresent(tasks::add);
                var collectorTask = collectorTaskSetService.getCollectorTask(monitorType, ipInterface, node.getId(), null);
                Optional.ofNullable(collectorTask).ifPresent(tasks::add);
            });
        });
        return tasks;
    }

    private void removeAssociatedTags(Node node) {
        for (Tag tag : node.getTags()) {
            tag.getNodes().remove(node);
        }
    }

    @Transactional(readOnly = true)
    public List<NodeDTO> listNodesByNodeLabelSearch(String tenantId, String nodeLabelSearchTerm) {
        List<Node> all = nodeRepository.findByTenantIdAndNodeLabelLike(tenantId, nodeLabelSearchTerm);
        return mapper.modelToDto(all);
    }

    @Transactional(readOnly = true)
    public List<NodeDTO> listNodesByTags(String tenantId, List<String> tags) {
        List<Node> all = nodeRepository.findByTenantIdAndTagNamesIn(tenantId, tags);
        return mapper.modelToDto(all);
    }
}