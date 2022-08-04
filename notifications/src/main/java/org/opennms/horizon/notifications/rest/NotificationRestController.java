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

package org.opennms.horizon.notifications.rest;

import org.opennms.horizon.notifications.kafka.KafkaProducer;
import org.opennms.horizon.notifications.service.NotificationService;
import org.opennms.horizon.shared.dto.event.AlarmDTO;
import org.opennms.horizon.shared.dto.event.EventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationRestController {

    @Autowired
    private NotificationService notificationsService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping("/pagerDutyKey")
    public ResponseEntity<String> getPagerDutyKey() {
        String key = notificationsService.getPagerDutyKey();
        return new ResponseEntity<>(key, HttpStatus.OK);
    }

    /*
     * Added only for test purposes
     */
    @PostMapping("/publishKafkaEvent")
    public ResponseEntity<EventDTO> publishKafkaEvent(@RequestBody EventDTO event) {
        kafkaProducer.send(event);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    /*
     * Added only for test purposes
     */
    @PostMapping("/publishKafkaAlarm")
    public ResponseEntity<AlarmDTO> publishKafkaAlarm(@RequestBody AlarmDTO alarm) {
        kafkaProducer.send(alarm);
        return new ResponseEntity<>(alarm, HttpStatus.CREATED);
    }
}
