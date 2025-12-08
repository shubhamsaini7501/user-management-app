package com.userManagementApp.service;

import com.userManagementApp.entity.Role;
import com.userManagementApp.security.JwtUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.userManagementApp.dto.JwtResponse;
import com.userManagementApp.dto.LoginRequest;
import com.userManagementApp.entity.User;
import com.userManagementApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;

    public JwtResponse login(LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            userService.updateLastLogin(user.getId());
            String jwt = jwtUtil.generateToken(authentication);
            userService.updateLastLogin(user.getId());

            kafkaProducerService.sendUserLoggedInEvent(user.getId(), user.getEmail());

            System.out.println("User logged in: " + user.getId());

            JwtResponse response = new JwtResponse();
            response.setToken(jwt);
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRoles(user.getRoles().stream().map(Role::getName).toArray(String[]::new));

            return response;
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid credentials", e);
        }

    }
}