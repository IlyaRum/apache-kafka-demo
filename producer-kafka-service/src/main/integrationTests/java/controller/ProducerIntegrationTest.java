package controller;

import my.example.producerkafkaservice.ProducerKafkaServiceApplication;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ProducerKafkaServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        partitions = 1,
        topics = {"example-topic"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@Testcontainers
public class ProducerIntegrationTest {

    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withEmbeddedZookeeper()
            .withExposedPorts(9093);

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Value("${test.topic}")
    private String topic;

    @Autowired
    private TestRestTemplate restTemplate;

    private AdminClient adminClient;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private KafkaConsumer<String, String> consumer;

    @BeforeEach
    void setup() throws IOException {
        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps("group", "false", embeddedKafkaBroker));
        consumer = new KafkaConsumer<>(configs, new StringDeserializer(), new StringDeserializer());
        consumer.subscribe(List.of(topic));

        adminClient = AdminClient.create(configs);
    }

    @AfterEach
    void cleanup() {
        consumer.close();
        adminClient.close();
    }

    @Test
    void testSendMessage() {
        String message = "test message";

        ResponseEntity<Void> response = restTemplate.exchange(
                "/send-message?message={message}", HttpMethod.POST, null, Void.class, message);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
        List<String> receivedMessages = new ArrayList<>();

        for (ConsumerRecord<String, String> record : records) {
            receivedMessages.add(record.value());
        }

        assertThat(receivedMessages).containsExactly(message);
    }
}