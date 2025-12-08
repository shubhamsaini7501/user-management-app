package com.userManagementApp.controller;

import com.userManagementApp.dto.RegisterRequest;
import com.userManagementApp.mapper.UserMapper;
import com.userManagementApp.dto.UserResponse;
import com.userManagementApp.entity.User;
import com.userManagementApp.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User registeredUser = userService.register(request);
        return ResponseEntity.ok(userMapper.toUserResponse(registeredUser));
    }

    @GetMapping("/me")
    @Cacheable(value = "user-profile", key = "#authentication.name")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        // Fetch user by email
        User user = userService.getByEmail(email);
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }
}
