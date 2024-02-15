package org.example.mockwarehouse;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StorageAssignmentConsumer {
    @KafkaListener(topics = "storage_assignment_1")
    public void listen(String message) {
        System.out.println(message);
    }
}
