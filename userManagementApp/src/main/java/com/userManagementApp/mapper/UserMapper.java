package com.userManagementApp.mapper;

import com.userManagementApp.dto.UserResponse;
import com.userManagementApp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {com.userManagementApp.entity.Role.class, java.util.stream.Collectors.class})
public interface UserMapper {

    @Mapping(target = "username", expression = "java(user.getUsername())")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))")
    UserResponse toUserResponse(User user);
}
