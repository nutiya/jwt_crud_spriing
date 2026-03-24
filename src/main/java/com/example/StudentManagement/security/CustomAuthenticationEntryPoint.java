package com.example.StudentManagement.security;

import com.example.StudentManagement.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // injected by Spring

    //custom format to show error response in api
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                401,
                "You must login first",
                null,
                request.getRequestURI()
        );

        //converts the Java object apiResponse to JSON and sends it as the HTTP response
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}