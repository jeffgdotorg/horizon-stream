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
package org.opennms.miniongateway.grpc.server.kafka;

import com.google.protobuf.Message;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.opennms.horizon.shared.grpc.common.TenantIDGrpcServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * A helper class which produces kafka messages.
 *
 * It additionally retrieves tenant information from present context.
 */
public class KafkaGrpcMessagePublisher {
    public final static String TENANT_ID_HEADER_NAME = "tenant-id";

    private final Logger logger = LoggerFactory.getLogger(KafkaGrpcMessagePublisher.class);
    private String messageKind;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final TenantIDGrpcServerInterceptor tenantInterceptor;
    private final String topic;

    public KafkaGrpcMessagePublisher(String messageKind, KafkaTemplate<String, byte[]> kafkaTemplate, TenantIDGrpcServerInterceptor tenantInterceptor, String topic) {
        this.messageKind = messageKind;
        this.kafkaTemplate = kafkaTemplate;
        this.tenantInterceptor = tenantInterceptor;
        this.topic = topic;
    }

    /**
     * Format the record to send to Kafka, with the needed content and the headers.
     *
     * @param content content to include as the message payload.
     * @return ProducerRecord to send to Kafka.
     */
    public void send(Message content) {
        logger.debug("Received {}; sending to Kafka: kafka-topic={}; message={}", messageKind, topic, content);
        String tenantId = tenantInterceptor.readCurrentContextTenantId();
        List<Header> headers = new LinkedList<>();
        headers.add(new RecordHeader(TENANT_ID_HEADER_NAME, tenantId.getBytes(StandardCharsets.UTF_8)));

        kafkaTemplate.send(new ProducerRecord<String, byte[]>(
            topic,
            null,
            null,
            content.toByteArray(),
            headers
        ));
    }
}
