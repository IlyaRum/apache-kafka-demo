package controller;

import Config.KafkaTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {KafkaTestConfig.class})
@TestPropertySource(locations = "classpath:application.yaml")
public class ConsumerIntegrationTest {

    @Value("${test.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testListenMethod() throws Exception {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("consume-topic", "test-message").toCompletableFuture();
        SendResult<String, String> sendResult = future.get();
        assertThat(sendResult).isNotNull();
        assertEquals(sendResult.getProducerRecord().value(), "test-message");
    }
}