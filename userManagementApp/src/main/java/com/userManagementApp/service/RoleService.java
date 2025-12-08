package com.userManagementApp.service;


import com.userManagementApp.dto.AssignRoleRequest;
import com.userManagementApp.dto.RoleRequest;
import com.userManagementApp.entity.Role;
import com.userManagementApp.entity.User;
import com.userManagementApp.repository.RoleRepository;
import com.userManagementApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

   @Transactional
   public Role createRole(RoleRequest request) {

       Set<String> allowedRoles = Set.of("ADMIN", "USER");
       String roleName = request.getName().toUpperCase();

       // Validate allowed roles
       if (!allowedRoles.contains(roleName)) {
           throw new IllegalArgumentException("Invalid role. Allowed roles: ADMIN, USER");
       }

       // Existing check
       if (roleRepository.existsByName(roleName)) {
           throw new IllegalArgumentException("Role already exists");
       }

       Role role = new Role();
       role.setName(roleName);
       Role saved = roleRepository.save(role);
       System.out.println("Role created: " + saved.getId());

       return saved;
   }


    @Transactional
    public Set<String> assignRolesToUser(Long userId, AssignRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<Role> roles = request.getRoleNames().stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " +
                                name)))
                .collect(Collectors.toSet());

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userRepository.save(user);

        System.out.println("Roles assigned to user " + userId + ": " + request.getRoleNames());

        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public Role createRoleIfNotExists(String name) {
        if (!roleRepository.existsByName(name)) {
            RoleRequest req = new RoleRequest();
            req.setName(name);
            return createRole(req);
        }
        return roleRepository.findByName(name).get();
    }
}