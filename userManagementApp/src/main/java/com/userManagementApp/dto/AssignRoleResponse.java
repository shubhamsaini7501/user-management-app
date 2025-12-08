package com.userManagementApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AssignRoleResponse {
    private Long userId;
    private Set<String> rolesAssigned;
}
