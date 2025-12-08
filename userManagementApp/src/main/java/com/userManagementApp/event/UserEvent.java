package com.userManagementApp.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEvent {
    private String type;
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime timestamp;

    public UserEvent(String type, Long userId, String username, String email, LocalDateTime timestamp) {
        this.type = type;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.timestamp = timestamp;
    }
}