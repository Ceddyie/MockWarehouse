package org.example.mockwarehouse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table
public class Product {
    @Column
    private String productId;
    @Column
    private String productName;
    @Column
    private int amount;
}
