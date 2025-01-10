package Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTestConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
                .withEmbeddedZookeeper()
                .withExposedPorts(9093);
    }

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaContainer kafkaContainer) {
        kafkaContainer.start();
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        return new KafkaAdmin(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(KafkaContainer kafkaContainer) {
        kafkaContainer.start();
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaTemplate<>(new org.springframework.kafka.core.DefaultKafkaProducerFactory<>(producerProps));
    }
}