package com.userManagementApp.init;


import com.userManagementApp.entity.Role;
import com.userManagementApp.entity.User;
import com.userManagementApp.repository.RoleRepository;
import com.userManagementApp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        System.out.println("=== Initializing default data ===");

        // Create default roles
        Role userRole = createRoleIfNotExists("USER");
        Role adminRole = createRoleIfNotExists("ADMIN");

        System.out.println("Default roles created");

        // Create a default admin user if not exists
        createDefaultAdminIfNotExists(adminRole);

        System.out.println("=== Data initialization complete ===");
    }

    private Role createRoleIfNotExists(String roleName) {
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if (existingRole.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            Role saved = roleRepository.save(role);
            System.out.println("Created role: " + roleName);
            return saved;
        }
        System.out.println("Role already exists: " + roleName);
        return existingRole.get();
    }

    private void createDefaultAdminIfNotExists(Role adminRole) {
        String adminEmail = "admin@example.com";
        String adminPassword = "Admin@123";

        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            System.out.println("✓ Default admin created");
            System.out.println("  Email: " + adminEmail);
            System.out.println("  Password: " + adminPassword);
        } else {
            System.out.println("Admin user already exists: " + adminEmail);
        }
    }
}