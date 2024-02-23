package org.example.mockwarehouse.answers;

import lombok.Data;
import org.example.mockwarehouse.requests.ProductSelectionRequest;

@Data
public class ProductLocation {
    private String productId;
    private int amount;
    private int storageLocation;
    private long timestamp;

    public ProductLocation (String productId, int amount, int storageLocation, long timestamp) {
        this.productId = productId;
        this.amount = amount;
        this.storageLocation = storageLocation;
        this.timestamp = timestamp;
    }
}
