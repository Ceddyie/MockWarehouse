package org.example.mockwarehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StorageAssignmentConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @KafkaListener(topics = "storage_assignment_1")
    public void listen(String message) throws JsonMappingException, JsonProcessingException {
        AssignmentRequest request = objectMapper.readValue(message, AssignmentRequest.class);
        log.info("{}", request);

        String productId = request.getProductId();
        int amount = request.getAmount();

        processAssignment(productId, amount);
    }

    private void processAssignment(String productId, int amount) {
        if (!checkForProduct(productId)) {
            System.out.println("Nicht vorhanden");
        } else {
            System.out.println("vorhanden");
        }
    }

    private boolean checkForProduct(String productId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("productId", productId);

        List<Map<String, Object>> requestList = jdbcTemplate.queryForList("SELECT * FROM storage_assignment WHERE product_id = :productId", namedParameters);

        return !requestList.isEmpty();
    }
}
