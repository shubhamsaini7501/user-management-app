package com.userManagementApp.service;

import com.userManagementApp.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendUserRegisteredEvent(Long userId, String username, String email) {
        UserEvent event = new UserEvent("REGISTERED", userId, username, email, java.time.LocalDateTime.now());
        try {
            kafkaTemplate.send("user-registered", String.valueOf(userId), event);
        } catch (Exception e) {
            System.out.println("Failed to send registered event: " + e.getMessage());
        }
    }

    public void sendUserLoggedInEvent(Long userId, String email) {
        UserEvent event = new UserEvent("LOGGED_IN", userId, null, email, java.time.LocalDateTime.now());
        try {
            kafkaTemplate.send("user-logged-in", String.valueOf(userId), event);
        } catch (Exception e) {
            System.out.println("Failed to send logged-in event: " + e.getMessage());
        }
    }
}