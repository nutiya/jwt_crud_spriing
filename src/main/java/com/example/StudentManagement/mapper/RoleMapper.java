package com.example.StudentManagement.mapper;

import com.example.StudentManagement.dto.request.RoleRequest;
import com.example.StudentManagement.dto.response.RoleResponse;
import com.example.StudentManagement.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toDto(Role role);

    Role toEntity(RoleRequest request);
}