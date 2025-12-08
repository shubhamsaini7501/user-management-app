package com.userManagementApp.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StatsResponse {

    private Long totalUsers;
    private List<UserLastLogin> lastLogins;

    @Data
    public static class UserLastLogin {
        private Long userId;
        private LocalDateTime lastLogin;
    }
}