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

package org.opennms.horizon.alertservice.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.opennms.horizon.alerts.proto.AlertType;
import org.opennms.horizon.alerts.proto.ManagedObjectType;
import org.opennms.horizon.model.common.proto.Severity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="alert")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Alert extends TenantAwareEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 7275548439687562161L;

    @Id
    @SequenceGenerator(name="alertSequence", sequenceName="alert_nxt_id", allocationSize = 1)
    @GeneratedValue(generator="alertSequence")
    @Column(nullable=false)
    private Long alertId;

    @Column(length=256, nullable=false)
    private String eventUei;

    @Column(unique=true)
    private String reductionKey;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private AlertType type;

    @Column(nullable=false)
    private Long counter;

    @Column(nullable=false)
    @Enumerated(EnumType.ORDINAL)
    private Severity severity = Severity.INDETERMINATE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date firstEventTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastEventTime;

    @Column
    private String description;

    @Column
    private String logMessage;

    @Column
    private String acknowledgedByUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date acknowledgedAt;

    @Column
    private Long lastEventId;

    @Column
    private String clearKey;

    @Column(nullable=false)
    @Enumerated(EnumType.ORDINAL)
    private ManagedObjectType managedObjectType;

    @Column
    private String managedObjectInstance;

    public void incrementCount() {
        counter++;
    }
}