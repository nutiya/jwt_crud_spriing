package com.example.StudentManagement.mapper;

import com.example.StudentManagement.dto.request.GenderRequest;
import com.example.StudentManagement.dto.response.GenderResponse;
import com.example.StudentManagement.entity.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface GenderMapper {

    GenderResponse toDto(Gender gender);
    Gender toEntity(GenderRequest request);
    void update(GenderRequest request, @MappingTarget Gender gender);
}
