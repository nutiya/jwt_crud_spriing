package com.example.StudentManagement.mapper;


import com.example.StudentManagement.dto.request.StudentRequest;
import com.example.StudentManagement.dto.response.StudentResponse;
import com.example.StudentManagement.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(source = "gender.name", target = "gender")
    StudentResponse toDto(Student student);

    Student toEntity(StudentRequest request);

    @Mapping(target = "createdAt", ignore = true)
    void update(StudentRequest request, @MappingTarget Student student);


}
