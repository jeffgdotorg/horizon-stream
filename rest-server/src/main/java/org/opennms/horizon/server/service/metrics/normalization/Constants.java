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

package org.opennms.horizon.server.service.metrics.normalization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String AZURE_MONITOR_TYPE = "AZURE";
    public static final String SNMP_MONITOR_TYPE = "SNMP";

    public static final String DISCOVERY_SCAN = "DISCOVERY_SCAN";
    public static final String AZURE_SCAN_TYPE = "AZURE_SCAN";
    public static final String NODE_SCAN = "NODE_SCAN";

    // Common Metric Name
    public static final String NETWORK_IN_TOTAL_BYTES = "network_in_total_bytes";
    public static final String NETWORK_OUT_TOTAL_BYTES = "network_out_total_bytes";


    public static final String QUERY_PREFIX = "query=";
    public static final String NETWORK_IN_BITS = "network_in_bits";
    public static final String NETWORK_OUT_BITS = "network_out_bits";

    public static final String QUERY_FOR_TOTAL_NETWORK_IN_BITS = "irate(ifHCInOctets[4m])*8";
    public static final String QUERY_FOR_TOTAL_NETWORK_OUT_BITS = "irate(ifHCOutOctets[4m])*8";

    public static final String BW_IN_PERCENTAGE = "bw_util_network_in";
    public static final String BW_OUT_PERCENTAGE = "bw_util_network_out";

    public static final String QUERY_FOR_BW_IN_UTIL_PERCENTAGE = "(irate(ifHCInOctets[4m])*8) / (ifHighSpeed *1000000) * 100 unless ifHighSpeed == 0";
    public static final String QUERY_FOR_BW_OUT_UTIL_PERCENTAGE = "(irate(ifHCOutOctets[4m])*8) / (ifHighSpeed *1000000) * 100 unless ifHighSpeed == 0";

    // SNMP Specific Metric Names
    public static final String IF_IN_OCTETS = "ifInOctets";
    public static final String IF_OUT_OCTETS = "ifOutOctets";
    public static final String SYS_UP_TIME = "sysUpTime";

    // Total Network
    public static final String TOTAL_NETWORK_BYTES_IN = "total_network_bytes_in";
    public static final String TOTAL_NETWORK_BYTES_OUT = "total_network_bytes_out";
    public static final String QUERY_FOR_TOTAL_NETWORK_BYTES_IN = "sum(rate(ifHCInOctets[1h])*3600)";
    public static final String QUERY_FOR_TOTAL_NETWORK_BYTES_OUT = "sum(rate(ifHCOutOctets[1h])*3600)";
}
