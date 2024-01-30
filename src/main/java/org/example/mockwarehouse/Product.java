package org.example.mockwarehouse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
public class Product {
    @Id
    private String productId;

    private String productName;

    private int amount;
}
