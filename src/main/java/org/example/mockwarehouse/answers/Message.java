package org.example.mockwarehouse.answers;

import lombok.Data;

@Data
public class Message {
    String message;
    long timestamp;

    public Message(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
