package org.example.mockwarehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String PRODUCT_SELECTION_TOPIC = "product_selection_2";
    private static final String STORAGE_ASSIGNMENT_TOPIC = "storage_assignment_2";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendProductLocation(String productId, int amount, String storageLocation) {
        String message = productId + ":" + amount + ":" + storageLocation;
        kafkaTemplate.send(PRODUCT_SELECTION_TOPIC, message);
    }

    public void sendError() {
        String message = "Error";
        kafkaTemplate.send(PRODUCT_SELECTION_TOPIC, message);
    }
}
