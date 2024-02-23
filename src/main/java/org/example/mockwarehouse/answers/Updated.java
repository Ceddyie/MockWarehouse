package org.example.mockwarehouse.answers;

import lombok.Data;

@Data
public class Updated {
    private String productId;
    private int storageLocation;
    private int amount;
    private long timestamp;

    public Updated(String productId, int storageLocation, int amount, long timestamp) {
        this.productId = productId;
        this.storageLocation = storageLocation;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
