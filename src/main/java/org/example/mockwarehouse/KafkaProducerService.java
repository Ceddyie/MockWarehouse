package org.example.mockwarehouse;

import org.example.mockwarehouse.answers.Assigned;
import org.example.mockwarehouse.answers.Message;
import org.example.mockwarehouse.answers.ProductLocation;
import org.example.mockwarehouse.answers.Updated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String PRODUCT_SELECTION_TOPIC = "product_selection_2";
    private static final String PRODUCT_SELECTION_ERROR = "product_selection_error";
    private static final String STORAGE_ASSIGNMENT_TOPIC = "storage_assignment_2";

    @Autowired
    private KafkaTemplate<String, Object> objectKafkaTemplate;

    ///////////////// PRODUCT SELECTION /////////////////
    public void sendProductLocation(String productId, int amount, int storageLocation) {
        objectKafkaTemplate.send(PRODUCT_SELECTION_TOPIC, new ProductLocation(productId, amount, storageLocation, System.currentTimeMillis()));
    }

    public void sendError() {
        String message = "Error! Please check the information you entered.";
        objectKafkaTemplate.send(PRODUCT_SELECTION_ERROR, new Message(message, System.currentTimeMillis()));
    }

    public void sendAmountError() {
        String message = "Error: Amount too high, not enough in storage!";
        objectKafkaTemplate.send(PRODUCT_SELECTION_ERROR, new Message(message, System.currentTimeMillis()));
    }

    public void sendLastItem(String productId) {
        String message = "Warning: Last item picked, product with ID: " + productId + " removed from storage!";
        objectKafkaTemplate.send(PRODUCT_SELECTION_ERROR, new Message(message, System.currentTimeMillis()));
    }

    ///////////////// STORAGE ASSIGNMENT //////////////////
    public void sendSuccessfullyAssigned(String productId, int storageLocation) {
        objectKafkaTemplate.send(STORAGE_ASSIGNMENT_TOPIC, new Assigned(productId, storageLocation, System.currentTimeMillis()));
    }

    public void sendUpdated(String productId, int storageLocation, int amount) {
        objectKafkaTemplate.send(STORAGE_ASSIGNMENT_TOPIC, new Updated(productId, storageLocation, amount, System.currentTimeMillis()));
    }
}
