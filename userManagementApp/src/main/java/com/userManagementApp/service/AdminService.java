package com.userManagementApp.service;

import com.userManagementApp.dto.StatsResponse;
import com.userManagementApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Cacheable("stats")
    public StatsResponse getStats() {
        StatsResponse response = new StatsResponse();
        response.setTotalUsers(userRepository.count());
        response.setLastLogins(userRepository.findAll().stream()
                .filter(u -> u.getLastLogin() != null)
                .limit(10)
                .map(u -> {
                    StatsResponse.UserLastLogin ll = new StatsResponse.UserLastLogin();
                    ll.setUserId(u.getId());
                    ll.setLastLogin(u.getLastLogin());
                    return ll;
                })
                .collect(Collectors.toList()));
        return response;
    }
}