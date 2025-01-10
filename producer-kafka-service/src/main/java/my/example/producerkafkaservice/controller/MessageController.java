package my.example.producerkafkaservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/send-message")
    public void sendMessage(@RequestParam("message") String message) {
        kafkaTemplate.send("example-topic", message);
    }
}
