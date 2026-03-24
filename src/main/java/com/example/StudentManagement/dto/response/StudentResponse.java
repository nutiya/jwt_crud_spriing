package com.example.StudentManagement.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String gender;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
