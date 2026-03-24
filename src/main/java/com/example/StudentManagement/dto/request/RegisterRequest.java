package com.example.StudentManagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 2, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
