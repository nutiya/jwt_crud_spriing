package com.example.StudentManagement.exception;

import com.example.StudentManagement.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 🔴 401 (login fail)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", request);
    }

    // 🔴 401 (wrong password)
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse<Object>> handleWrongPassword(
            WrongPasswordException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // 🔴 409 duplicate
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 🔴 500 service
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleExternalService(
            ExternalServiceException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpired(
            TokenExpiredException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // 🔴 DB error
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDatabaseError(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate data in database", request);
    }

    // 🔴 Validation (IMPORTANT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        400,
                        "Validation failed",
                        fieldErrors, // 👈 send errors in data
                        request.getRequestURI()
                )
        );
    }

    // 🔴 Fallback (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    // 🔧 Common builder
    private ResponseEntity<ApiResponse<Object>> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status).body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        status.value(),
                        message,
                        null,
                        request.getRequestURI()
                )
        );
    }
}