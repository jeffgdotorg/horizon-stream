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

package org.opennms.horizon.server.service.metrics;

import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.server.model.TimeRangeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.opennms.horizon.server.service.metrics.Constants.AZURE_SCAN_TYPE;
import static org.opennms.horizon.server.service.metrics.Constants.BW_IN_PERCENTAGE;
import static org.opennms.horizon.server.service.metrics.Constants.BW_OUT_PERCENTAGE;
import static org.opennms.horizon.server.service.metrics.Constants.NETWORK_IN_BITS;
import static org.opennms.horizon.server.service.metrics.Constants.NETWORK_OUT_BITS;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_AZURE_TOTAL_NETWORK_IN_BITS;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_AZURE_TOTAL_NETWORK_OUT_BITS;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_BW_IN_UTIL_PERCENTAGE;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_BW_OUT_UTIL_PERCENTAGE;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_TOTAL_NETWORK_BYTES_IN;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_TOTAL_NETWORK_BYTES_OUT;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_TOTAL_NETWORK_IN_BITS;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_FOR_TOTAL_NETWORK_OUT_BITS;
import static org.opennms.horizon.server.service.metrics.Constants.QUERY_PREFIX;
import static org.opennms.horizon.server.service.metrics.Constants.TOTAL_NETWORK_BYTES_IN;
import static org.opennms.horizon.server.service.metrics.Constants.TOTAL_NETWORK_BYTES_OUT;

@Component
public class QueryService {

    private static final Logger LOG = LoggerFactory.getLogger(QueryService.class);

    public String getQueryString(String metricName, Map<String, String> labels) {
        Map<String, String> queryParams = new HashMap<>(labels);
        queryParams.put(MetricLabelUtils.METRIC_NAME_KEY, metricName);
        return getQueryString(queryParams);
    }

    public boolean isRangeQuery(String metricName) {
        return TOTAL_NETWORK_BYTES_IN.equals(metricName) || TOTAL_NETWORK_BYTES_OUT.equals(metricName)
            || NETWORK_IN_BITS.equals(metricName) || NETWORK_OUT_BITS.equals(metricName)
            || BW_IN_PERCENTAGE.equals(metricName) || BW_OUT_PERCENTAGE.equals(metricName);
    }

    public String getQueryString(Optional<NodeDTO> node, String metricName, Map<String, String> labels,
                                 Integer timeRange, TimeRangeUnit timeRangeUnit) {

        if (isRangeQuery(metricName)) {
            long end = System.currentTimeMillis() / 1000L;
            long start = end - getDuration(timeRange, timeRangeUnit).orElse(Duration.ofHours(24)).getSeconds();
            String rangeQuerySuffixForTotal = "&start=" + start + "&end=" + end +
                "&step=1h";
            String rangeQuerySuffix = "&start=" + start + "&end=" + end +
                "&step=2m";
            switch (metricName) {
                case TOTAL_NETWORK_BYTES_IN:
                    return QUERY_PREFIX + encode(QUERY_FOR_TOTAL_NETWORK_BYTES_IN) + rangeQuerySuffixForTotal;
                case TOTAL_NETWORK_BYTES_OUT:
                    return QUERY_PREFIX + encode(QUERY_FOR_TOTAL_NETWORK_BYTES_OUT) + rangeQuerySuffixForTotal;
                case NETWORK_IN_BITS:
                    if (isAzureNode(node)) {
                        return QUERY_PREFIX + QUERY_FOR_AZURE_TOTAL_NETWORK_IN_BITS + rangeQuerySuffix;
                    } else {
                        return QUERY_PREFIX + QUERY_FOR_TOTAL_NETWORK_IN_BITS + rangeQuerySuffix;
                    }
                case NETWORK_OUT_BITS:
                    if (isAzureNode(node)) {
                        return QUERY_PREFIX + QUERY_FOR_AZURE_TOTAL_NETWORK_OUT_BITS + rangeQuerySuffix;
                    } else {
                        return QUERY_PREFIX + QUERY_FOR_TOTAL_NETWORK_OUT_BITS + rangeQuerySuffix;
                    }
                case BW_IN_PERCENTAGE:
                    if (isAzureNode(node)) {
                        throw new RuntimeException("Operation not supported for Azure node: " + BW_IN_PERCENTAGE);
                    }
                    return QUERY_PREFIX + QUERY_FOR_BW_IN_UTIL_PERCENTAGE + rangeQuerySuffix;
                case BW_OUT_PERCENTAGE:
                    if (isAzureNode(node)) {
                        throw new RuntimeException("Operation not supported for Azure node: " + BW_OUT_PERCENTAGE);
                    }
                    return QUERY_PREFIX + QUERY_FOR_BW_OUT_UTIL_PERCENTAGE + rangeQuerySuffix;
            }
        }
        String queryString = getQueryString(metricName, labels);
        return addTimeRange(timeRange, timeRangeUnit, queryString);
    }


    public String getQueryString(Map<String, String> queryParams) {
        StringBuilder sb = new StringBuilder(QUERY_PREFIX + "{");

        int index = 0;
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            // tilde will treat the string as a regex
            sb.append(String.format("%s=~\"%s\"", param.getKey(), param.getValue()));
            if (index != queryParams.size() - 1) {
                sb.append(",");
            }
            index++;
        }

        sb.append("}");
        return sb.toString();
    }

    private String addTimeRange(Integer timeRange, TimeRangeUnit timeRangeUnit, String queryString) {
        if (timeRange != null && timeRangeUnit != null) {
            return queryString + "[" + timeRange + timeRangeUnit.value + "]";
        }
        return queryString;
    }

    public static Optional<Duration> getDuration(Integer timeRange, TimeRangeUnit timeRangeUnit) {
        try {
            if (TimeRangeUnit.DAY.value.equals(timeRangeUnit.value)) {
                return Optional.of(Duration.parse("P" + timeRange + timeRangeUnit.value));
            }
            return Optional.of(Duration.parse("PT" + timeRange + timeRangeUnit.value));
        } catch (Exception e) {
            LOG.warn("Exception while parsing time range with timeRange {} in units {}", timeRange, timeRangeUnit, e);
        }
        return Optional.empty();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private boolean isAzureNode(Optional<NodeDTO> node){
        return isNodeScanType(node, AZURE_SCAN_TYPE);
    }

    private boolean isNodeScanType(Optional<NodeDTO> node, String scanType) {
        return node.map(NodeDTO::getScanType).orElse("").equals(scanType);
    }
}
