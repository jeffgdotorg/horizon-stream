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

package org.opennms.horizon.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opennms.horizon.inventory.dto.MonitoringLocationCreateDTO;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.server.model.inventory.MonitoringLocation;
import org.opennms.horizon.server.model.inventory.MonitoringLocationCreate;
import org.opennms.horizon.server.model.inventory.MonitoringLocationUpdate;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface MonitoringLocationMapper {
    @Mapping(target = "geoLocation.latitude", source = "latitude")
    @Mapping(target = "geoLocation.longitude", source = "longitude")
    @Mapping(target = "address.id", source = "addressId")
    MonitoringLocationCreateDTO locationCreateToLocationCreateProto(MonitoringLocationCreate location);

    @Mapping(target = "geoLocation.latitude", source = "latitude")
    @Mapping(target = "geoLocation.longitude", source = "longitude")
    @Mapping(target = "address.id", source = "addressId")
    MonitoringLocationDTO locationUpdateToLocationProto(MonitoringLocationUpdate location);

    @Mapping(target = "geoLocation.latitude", source = "latitude")
    @Mapping(target = "geoLocation.longitude", source = "longitude")
    MonitoringLocationDTO locationToLocationProto(MonitoringLocation location);

    @Mapping(target = "latitude", source = "geoLocation.latitude")
    @Mapping(target = "longitude", source = "geoLocation.longitude")
    MonitoringLocation protoToLocation(MonitoringLocationDTO location);

}

