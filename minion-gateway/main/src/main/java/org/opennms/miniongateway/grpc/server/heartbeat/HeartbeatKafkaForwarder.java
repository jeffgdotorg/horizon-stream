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

package org.opennms.miniongateway.grpc.server.heartbeat;

import com.google.protobuf.Message;
import org.opennms.horizon.shared.grpc.common.TenantIDGrpcServerInterceptor;
import org.opennms.horizon.shared.ipc.sink.api.MessageConsumer;
import org.opennms.horizon.shared.ipc.sink.api.SinkModule;
import org.opennms.miniongateway.grpc.server.kafka.KafkaGrpcMessagePublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Forwarder of Heartbeat messages - received via GRPC and forwarded to Kafka.
 */
@Component
public class HeartbeatKafkaForwarder implements MessageConsumer<Message, Message> {

    public static final String DEFAULT_HEARTBEAT_RESULTS_TOPIC = "heartbeat";

    private final KafkaGrpcMessagePublisher kafkaPublisher;

    public HeartbeatKafkaForwarder(@Qualifier("kafkaByteArrayProducerTemplate") KafkaTemplate<String, byte[]> kafkaProducer,
        TenantIDGrpcServerInterceptor tenantIDGrpcInterceptor,
        @Value("${heartbeat.results.kafka-topic:" + DEFAULT_HEARTBEAT_RESULTS_TOPIC + "}") String kafkaTopic) {
        this.kafkaPublisher = new KafkaGrpcMessagePublisher("heartbeat", kafkaProducer, tenantIDGrpcInterceptor, kafkaTopic);
    }

    @Override
    public SinkModule<Message, Message> getModule() {
        return new HeartbeatModule();
    }

    @Override
    public void handleMessage(Message message) {
        this.kafkaPublisher.send(message);
    }
}
