package com.example.StudentManagement.controller;

import com.example.StudentManagement.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDateTime;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> buildResponse(
            T data, String message, int status, String path
    ) {
        return ResponseEntity.status(status).body(
                new ApiResponse<>(LocalDateTime.now(), status, message, data, path)
        );
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return buildResponse(data, message, 200, getCurrentPath());
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(
            T data, String message, URI location
    ) {
        return ResponseEntity
                .created(location)
                .body(new ApiResponse<>(LocalDateTime.now(), 201, message, data, getCurrentPath()));
    }


    private String getCurrentPath() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        return request.getRequestURI();
    }
}
