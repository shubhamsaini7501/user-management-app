package com.userManagementApp.service;

import com.userManagementApp.dto.RegisterRequest;
import com.userManagementApp.entity.Role;
import com.userManagementApp.entity.User;
import com.userManagementApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;
    private final RoleService roleService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);

        // Default role: Create "USER" if not exists, then assign
        Role userRole = roleService.createRoleIfNotExists("USER");
        saved.getRoles().add(userRole);
        saved = userRepository.save(saved);

        // Produce event
        kafkaProducerService.sendUserRegisteredEvent(saved.getId(), saved.getUsername(), saved.getEmail());
        // Audit log
        System.out.println("User registered: " + saved.getId());
        return saved;
    }

    @CacheEvict(value = "user-profile", allEntries = true)
    public void updateLastLogin(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }


    public User getByEmail(String email) {
        System.out.println("🔍 Fetching from DB: " + email); // LOG
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getByEmail(email);
    }

}