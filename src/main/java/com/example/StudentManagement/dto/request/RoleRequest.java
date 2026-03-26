package com.example.StudentManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequest {
    @NotBlank(message = "Role name is required")
    private String name;
}