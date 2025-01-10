package my.example.consumerkafkaservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(topics = "example-topic", groupId = "example-group")
    public void listen(String message) {
        logger.info("Received Message: {}", message);
    }
}
