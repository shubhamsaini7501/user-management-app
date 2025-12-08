package com.userManagementApp.controller;


import com.userManagementApp.dto.AssignRoleRequest;
import com.userManagementApp.dto.AssignRoleResponse;
import com.userManagementApp.dto.RoleRequest;
import com.userManagementApp.entity.Role;
import com.userManagementApp.service.RoleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<AssignRoleResponse> assignRoles(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRoleRequest request) {

        Set<String> updatedRoles = roleService.assignRolesToUser(userId, request);
        AssignRoleResponse response = new AssignRoleResponse(userId, updatedRoles);
        return ResponseEntity.ok(response);
    }
}
