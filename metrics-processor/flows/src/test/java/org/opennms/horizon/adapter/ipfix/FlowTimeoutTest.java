/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
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

package org.opennms.horizon.adapter.ipfix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.opennms.horizon.shared.flows.FlowMessage;
import org.opennms.horizon.shared.flows.transport.IpFixMessageBuilder;
import org.opennms.horizon.shared.flows.values.DateTimeValue;
import org.opennms.horizon.shared.flows.values.UnsignedValue;
import org.opennms.horizon.shared.flows.values.Value;

import com.google.common.collect.ImmutableList;

public class FlowTimeoutTest {

    @Test
    public void testWithoutTimeout() {
        // Given
        Iterable<Value<?>> values = ImmutableList.<Value<?>>builder()
            .add(new DateTimeValue("flowStartSeconds", Instant.ofEpochSecond(123)))
            .add(new DateTimeValue("flowEndSeconds", Instant.ofEpochSecond(987)))
            .build();

        // When
        final IpFixMessageBuilder ipFixMessageBuilder = new IpFixMessageBuilder();
        FlowMessage flowMessage = ipFixMessageBuilder.buildMessage(values, (address -> Optional.empty())).build();

        // Then
        assertEquals(flowMessage.getFirstSwitched().getValue(), 123000L);
        assertEquals(flowMessage.getDeltaSwitched().getValue(), 123000L); // Timeout is same as first
        assertEquals(flowMessage.getLastSwitched().getValue(), 987000L);
    }

    @Test
    public void testWithActiveTimeout() {
        // Given
        Iterable<Value<?>> values = ImmutableList.<Value<?>>builder()
            .add(new DateTimeValue("flowStartSeconds", Instant.ofEpochSecond(123)))
            .add(new DateTimeValue("flowEndSeconds", Instant.ofEpochSecond(987)))
            .add(new UnsignedValue("octetDeltaCount", 10))
            .add(new UnsignedValue("packetDeltaCount", 10))
            .add(new UnsignedValue("flowActiveTimeout", 10))
            .add(new UnsignedValue("flowInactiveTimeout", 300))
            .build();

        // When
        final IpFixMessageBuilder ipFixMessageBuilder = new IpFixMessageBuilder();
        FlowMessage flowMessage = ipFixMessageBuilder.buildMessage(values, (address -> Optional.empty())).build();

        // Then
        assertEquals(flowMessage.getFirstSwitched().getValue(), 123000L);
        assertEquals(flowMessage.getDeltaSwitched().getValue(), 987000L - 10000L);
        assertEquals(flowMessage.getLastSwitched().getValue(), 987000L);
    }

    @Test
    public void testWithInactiveTimeout() {
        // Given
        Iterable<Value<?>> values = ImmutableList.<Value<?>>builder()
            .add(new DateTimeValue("flowStartSeconds", Instant.ofEpochSecond(123)))
            .add(new DateTimeValue("flowEndSeconds", Instant.ofEpochSecond(987)))
            .add(new UnsignedValue("octetDeltaCount", 0))
            .add(new UnsignedValue("packetDeltaCount", 0))
            .add(new UnsignedValue("flowActiveTimeout", 10))
            .add(new UnsignedValue("flowInactiveTimeout", 300))
            .build();

        // When
        final IpFixMessageBuilder ipFixMessageBuilder = new IpFixMessageBuilder();
        FlowMessage flowMessage = ipFixMessageBuilder.buildMessage(values, (address -> Optional.empty())).build();

        // Then
        assertEquals(flowMessage.getFirstSwitched().getValue(), 123000L);
        assertEquals(flowMessage.getDeltaSwitched().getValue(), 987000L - 300000L);
        assertEquals(flowMessage.getLastSwitched().getValue(), 987000L);
    }


    @Test
    public void testFirstLastSwitchedValues() {
        // Given
        Iterable<Value<?>> values = ImmutableList.<Value<?>>builder()
            .add(new DateTimeValue("flowStartSeconds", Instant.ofEpochSecond(123)))
            .add(new DateTimeValue("flowEndSeconds", Instant.ofEpochSecond(987)))
            .build();

        // When
        IpFixMessageBuilder ipFixMessageBuilder = new IpFixMessageBuilder();
        FlowMessage flowMessage = ipFixMessageBuilder.buildMessage(values, (address -> Optional.empty())).build();

        // Then
        assertEquals(flowMessage.getFirstSwitched().getValue(), 123000L);
        assertEquals(flowMessage.getDeltaSwitched().getValue(), 123000L);
        assertEquals(flowMessage.getLastSwitched().getValue(), 987000L);

        // When
        values = ImmutableList.<Value<?>>builder()
            .add(new DateTimeValue("systemInitTimeMilliseconds", Instant.ofEpochMilli(100000)))
            .add(new UnsignedValue("flowStartSysUpTime", 2000000))
            .add(new UnsignedValue("flowEndSysUpTime", 4000000))
            .build();
        ipFixMessageBuilder = new IpFixMessageBuilder();
        flowMessage = ipFixMessageBuilder.buildMessage(values, (address -> Optional.empty())).build();

        // Then
        assertEquals(flowMessage.getFirstSwitched().getValue(), 2000000L + 100000L);
        assertEquals(flowMessage.getDeltaSwitched().getValue(), 2000000L + 100000L);
        assertEquals(flowMessage.getLastSwitched().getValue(), 4100000L);
    }
}
