package org.example.mockwarehouse.requests;

import lombok.Data;

@Data
public class AssignmentRequest {
    private String productId;
    private int amount;

    public AssignmentRequest() {}

    public AssignmentRequest(String productId, int amount) {
        this.amount = amount;
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "AssignmentRequest{"
                + "productId = " + productId
                + ", amount = " + amount
                + "}";
    }
}
