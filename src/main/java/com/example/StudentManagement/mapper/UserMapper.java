package com.example.StudentManagement.mapper;


import com.example.StudentManagement.dto.request.RegisterRequest;
import com.example.StudentManagement.dto.response.UserResponse;
import com.example.StudentManagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
    User toEntity(RegisterRequest request);


//    @Mapping(target = "password", ignore = true)
//    void update(RegisterRequest request, @MappingTarget User user);

}
