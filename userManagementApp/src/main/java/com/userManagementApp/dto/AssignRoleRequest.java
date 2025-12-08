package com.userManagementApp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class AssignRoleRequest {
    @NotEmpty
    private Set<String> roleNames;
}