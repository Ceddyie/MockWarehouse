package org.example.mockwarehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProductSelectionConsumer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "product_selection_1")
    public void listen(String message) {
        try {
            System.out.println("Kafka Message: " + message);
            String[] split = Arrays.stream(message.split(":")).map(String::trim).toArray(String[]::new);
            String productId = split[0].replace("\"", "");
            try {
                int amount = Integer.parseInt(split[1].replace("\"", ""));
                System.out.println("Product ID: " + productId);
                System.out.println("Amount: " + amount);
                checkDatabase(productId, amount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Amount must be a valid integer.");
                kafkaProducerService.sendError();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            kafkaProducerService.sendError();
        }

    }

    private void checkDatabase(String productId, int amount) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("productId", productId);
        try {
            if (!productId.isEmpty() || amount > 0) {
                int storageLocation = jdbcTemplate.queryForObject("SELECT storage_location FROM storage_assignment WHERE product_id = :productId", namedParameters, Integer.class);
                System.out.println(storageLocation);
                namedParameters.addValue("storageLocation", storageLocation);
                int oldAmount = jdbcTemplate.queryForObject("SELECT amount FROM storage_assignment WHERE product_id = :productId AND storage_location = :storageLocation", namedParameters, Integer.class);
                if (amount > oldAmount) {
                    kafkaProducerService.sendAmountError();
                } else {
                    int newAmount = oldAmount - amount;
                    if (newAmount == 0) {
                        jdbcTemplate.update("DELETE FROM storage_assignment WHERE product_id = :productId", namedParameters);
                        kafkaProducerService.sendLastItem(productId);
                    } else {
                        namedParameters.addValue("amount", newAmount);
                        jdbcTemplate.update("UPDATE storage_assignment SET amount = :amount WHERE storage_location = :storageLocation and product_id = :productId", namedParameters);
                        kafkaProducerService.sendProductLocation(productId, newAmount, storageLocation);
                    }

                }
            }
            else {
                kafkaProducerService.sendError();
            }

        } catch (IncorrectResultSizeDataAccessException e) {
            System.out.println(e.getMessage());
            kafkaProducerService.sendError();
        }
    }

}
