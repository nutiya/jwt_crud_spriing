package com.example.StudentManagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "Name is required.")
    @Size(min = 2, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email is invalid")
    private String email;
}
