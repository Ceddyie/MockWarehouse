package org.example.mockwarehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mockwarehouse.requests.ProductSelectionRequest;
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
            ObjectMapper objectMapper = new ObjectMapper();
            ProductSelectionRequest request = objectMapper.readValue(message, ProductSelectionRequest.class);
            long measureTime = System.currentTimeMillis() - request.getTimestamp();
            System.out.println("Measure time: " + measureTime);
            System.out.println("Kafka Object: " + request);
            checkDatabase(request.getProductId(), request.getAmount());
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
                System.out.println("Storage Location: " + storageLocation);
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
