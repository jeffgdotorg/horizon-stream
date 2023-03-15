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

package org.opennms.horizon.alertservice.db.repository;

import org.opennms.horizon.alerts.proto.AlertDefinition;
import org.opennms.horizon.alerts.proto.AlertType;
import org.opennms.horizon.alertservice.db.entity.Alert;
import org.opennms.horizon.events.proto.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * A temporary repository for alert definitions.
 *
 * Contains a couple fixed definitions to test basic behavior, and will evolve to store entities properly in the DB.
 */
@Service
public interface AlertDefinitionRepository extends JpaRepository<AlertDefinition, Long> {

    public List<AlertDefinition> findAll();
    public List<AlertDefinition> findById(long id);

    /*
    public List<AlertDefinition> findAll() {
        AlertDefinition ciscoLinkDownAlertDef = AlertDefinition.newBuilder()
            .setUei("uei.opennms.org/vendor/cisco/traps/SNMP_Link_Down")
            .setReductionKey("%s:%s:%d")
            .setType(AlertType.PROBLEM_WITH_CLEAR)
            .build();
        AlertDefinition ciscoLinkUpAlertDef = AlertDefinition.newBuilder()
            .setUei("uei.opennms.org/vendor/cisco/traps/SNMP_Link_Up")
            .setReductionKey("%s:%s:%d")
            .setClearKey("%s:uei.opennms.org/vendor/cisco/traps/SNMP_Link_Down:%d")
            .setType(AlertType.CLEAR)
            .build();
        return Arrays.asList(ciscoLinkDownAlertDef, ciscoLinkUpAlertDef);
    }*/

    default AlertDefinition getAlertDefinitionForEvent(Event event) {
        return findAll().stream()
            .filter(def -> event.getUei().equals(def.getUei()))
            .findFirst()
            .orElse(null);
    }
}
