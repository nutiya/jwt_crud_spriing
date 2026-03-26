package com.example.StudentManagement.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private  String email;
    private String roles;
}
