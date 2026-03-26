package com.example.StudentManagement.mapper;


import com.example.StudentManagement.dto.request.RegisterRequest;
import com.example.StudentManagement.dto.request.UpdateUserRequest;
import com.example.StudentManagement.dto.response.UserResponse;
import com.example.StudentManagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    default UserResponse toDto(User user) {
        String rolesString = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(", ")); // join with comma

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                rolesString
        );
    }
    User toEntity(RegisterRequest request);


    void update(UpdateUserRequest request, @MappingTarget User user);

}
