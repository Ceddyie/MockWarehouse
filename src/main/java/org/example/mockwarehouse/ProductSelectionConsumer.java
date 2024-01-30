package org.example.mockwarehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
    JdbcTemplate jdbcTemplate;


    @KafkaListener(topics = "product_selection_topic")
    public void listen(String message) {
        try {
            System.out.println(message);
            String[] split = Arrays.stream(message.split(":")).map(String::trim).toArray(String[]::new);
            String productId = split[0];
            String amountString = split[1].replace("\"", "");
            try {
                int amount = Integer.parseInt(amountString);
                System.out.println("Amount: " + amount);

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("productId", productId);
                namedParameters.addValue("amount", amount);

                String name = jdbcTemplate.queryForObject("SELECT product_name FROM product WHERE product_id = :productId and amount = :amount", String.class, namedParameters);
                System.out.println(name);
                /*try {
                    String name = jdbcTemplate.queryForObject("SELECT product_name FROM products WHERE product_id = :productId and amount = :amount", String.class, namedParameters);
                    System.out.println(name);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }*/
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Amount must be a valid integer.");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
