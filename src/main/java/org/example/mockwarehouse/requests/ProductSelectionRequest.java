package org.example.mockwarehouse.requests;

import lombok.Data;

@Data
public class ProductSelectionRequest {
    private String productId;
    private int amount;
    private long timestamp;
}
