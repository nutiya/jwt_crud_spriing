package com.example.StudentManagement.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//generic
@AllArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({ "timestamp", "status", "message", "data", "path" })
public class ApiResponse<T>{
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private T data;
    private String path;
}