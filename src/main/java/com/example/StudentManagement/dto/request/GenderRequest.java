package com.example.StudentManagement.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenderRequest {

    @NotBlank(message = "Name is required")
    private String name;

}
