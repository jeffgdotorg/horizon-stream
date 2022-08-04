package org.opennms.horizon.notifications.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.opennms.horizon.shared.dto.event.AlarmDTO;
import org.opennms.horizon.shared.dto.event.EventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(topics = {
    "${horizon.kafka.events.topic}", "${horizon.kafka.events.topic}"
})
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
class KafkaConsumerIntegrationTest {
    private static final int KAFKA_TIMEOUT = 5000;

    @Value("${horizon.kafka.events.topic}")
    private String eventTopic;

    @Value("${horizon.kafka.alarms.topic}")
    private String alarmsTopic;

    private Producer<String, EventDTO> eventProducer;
    private Producer<String, AlarmDTO> alarmProducer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @SpyBean
    private KafkaConsumer kafkaConsumer;

    @Captor
    ArgumentCaptor<EventDTO> eventCaptor;

    @Captor
    ArgumentCaptor<AlarmDTO> alarmCaptor;

    @BeforeAll
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));

        DefaultKafkaProducerFactory<String, EventDTO> eventFactory
            = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new JsonSerializer<>());
        eventProducer = eventFactory.createProducer();

        DefaultKafkaProducerFactory<String, AlarmDTO> alarmFactory
            = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new JsonSerializer<>());
        alarmProducer = alarmFactory.createProducer();
    }

    @Test
    void testEventKafkaConsumer() {
        EventDTO event = new EventDTO();
        event.setId(1234567);

        eventProducer.send(new ProducerRecord<>(eventTopic, event));
        eventProducer.flush();

        verify(kafkaConsumer, timeout(KAFKA_TIMEOUT).times(1))
            .consume(eventCaptor.capture());

        EventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(event.getId(), capturedEvent.getId());
    }

    @Test
    void testAlarmKafkaConsumer() {
        AlarmDTO alarm = new AlarmDTO();
        alarm.setId(7654321);

        alarmProducer.send(new ProducerRecord<>(alarmsTopic, alarm));
        alarmProducer.flush();

        verify(kafkaConsumer, timeout(KAFKA_TIMEOUT).times(1))
            .consume(alarmCaptor.capture());

        AlarmDTO capturedAlarm = alarmCaptor.getValue();
        assertEquals(alarm.getId(), capturedAlarm.getId());
    }

    @AfterAll
    void shutdown() {
        alarmProducer.close();
        eventProducer.close();
    }
}
