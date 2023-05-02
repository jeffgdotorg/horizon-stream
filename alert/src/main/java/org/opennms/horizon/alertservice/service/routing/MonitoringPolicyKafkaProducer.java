package org.opennms.horizon.alertservice.service.routing;

import jakarta.persistence.PostUpdate;
import jakarta.persistence.PostPersist;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.opennms.horizon.alerts.proto.MonitorPolicyProto;
import org.opennms.horizon.alertservice.db.entity.MonitorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MonitoringPolicyKafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private final String kafkaTopic;

    public MonitoringPolicyKafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate, KafkaTopicProperties kafkaTopicProperties) {
        this.kafkaTopic = kafkaTopicProperties.getMonitoringPolicy().getName();
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostUpdate
    @PostPersist
    public void sendMonitoringPolicy(MonitorPolicy monitorPolicy) {
        // Not all fields are included in this proto, since the Notification service doesn't care about all of them.
        MonitorPolicyProto proto = MonitorPolicyProto.newBuilder()
            .setId(monitorPolicy.getId())
            .setTenantId(monitorPolicy.getTenantId())
            .setNotifyByEmail(monitorPolicy.getNotifyByEmail())
            .setNotifyByWebhooks(monitorPolicy.getNotifyByWebhooks())
            .setNotifyByPagerDuty(monitorPolicy.getNotifyByPagerDuty())
            .build();

        var record = new ProducerRecord<>(kafkaTopic, toKey(monitorPolicy), proto.toByteArray());
        kafkaTemplate.send(record);
    }

    private String toKey(MonitorPolicy monitorPolicy) {
        return monitorPolicy.getId().toString();
    }
}

