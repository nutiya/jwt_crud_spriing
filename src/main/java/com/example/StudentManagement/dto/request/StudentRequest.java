package com.example.StudentManagement.dto.request;

import com.example.StudentManagement.entity.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Data
public class StudentRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Min(value = 1, message = "Age must be greater than 0")
    @NotNull(message = "Age is required")
    private Integer age;

    @NotNull(message = "Gender is required")
    private Long genderId;

    @NotNull(message = "Phone is required")
    @Size(min = 9, message = "Password must be at least 9 numbers")
    private String phone;

    @NotNull(message = "Address is required")
    private String address;




}
