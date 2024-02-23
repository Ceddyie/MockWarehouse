package org.example.mockwarehouse.answers;

import lombok.Data;

@Data
public class Assigned {
    private String productId;
    private int storageLocation;
    private long timestamp;

    public Assigned(String productId, int storageLocation, long timestamp) {
        this.productId = productId;
        this.storageLocation = storageLocation;
        this.timestamp = timestamp;
    }
}
