/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.alarms.db.impl.dto;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import org.opennms.horizon.db.model.OnmsMonitoringSystem;

/**
 * Represents an OpenNMS Distributed Poller.
 */
@Entity
@DiscriminatorValue(org.opennms.horizon.db.model.OnmsMonitoringSystem.TYPE_OPENNMS)
@XmlRootElement(name="distPoller")
public class DistPollerDTO extends OnmsMonitoringSystem implements Serializable {

    private static final long serialVersionUID = -1094353783612066524L;

    /**
     * default constructor
     */
    public DistPollerDTO() {}

    /**
     * minimal constructor
     *
     * @param id a {@link String} object.
     */
    public DistPollerDTO(String id) {
        // org.opennms.netmgt.dao.api.MonitoringLocationDao.DEFAULT_MONITORING_LOCATION_ID
        super(id, "Default");
    }
}
