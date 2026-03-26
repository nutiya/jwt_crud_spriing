package com.example.StudentManagement.controller;

import com.example.StudentManagement.dto.request.LoginRequest;
import com.example.StudentManagement.dto.request.RefreshTokenRequest;
import com.example.StudentManagement.dto.request.RegisterRequest;
import com.example.StudentManagement.dto.response.LoginResponse;
import com.example.StudentManagement.dto.response.UserResponse;
import com.example.StudentManagement.exception.WrongPasswordException;
import com.example.StudentManagement.security.JwtUtil;
import com.example.StudentManagement.service.AuthService;
import com.example.StudentManagement.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController extends BaseController{


    private final JwtUtil jwtUtil;
    private final AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            UriComponentsBuilder uriBuilder
            ) {

        var data = authService.register(request);
        var uri = uriBuilder.path("api/users/{id}").buildAndExpand(data.getId()).toUri();
        return created(data, "User registered successfully",uri);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        String[] tokens = authService.login(request).split(",");
        LoginResponse response = new LoginResponse(tokens[0], tokens[1]);
        return ok(response, "User logged in successfully");
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new WrongPasswordException("Invalid token");
        }

        String token = authHeader.substring(7);
        Date expiryDate = jwtUtil.getExpirationDate(token);
        authService.logout(token, expiryDate);

        return ok((Void)null, "User logged out successfully");
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {

        LoginResponse response = authService.refreshAccessToken(request.getRefreshToken());

        return ok(response, "Access & Refresh token rotated successfully");
    }


//    @PostMapping("/refresh-token")
//    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody String refreshToken) {
//        String newAccessToken = authService.refreshAccessToken(refreshToken);
//        LoginResponse response = new LoginResponse(newAccessToken, refreshToken);
//        return ok(response, "Access token refreshed successfully");
//    }
}