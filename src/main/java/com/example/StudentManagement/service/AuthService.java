package com.example.StudentManagement.service;

import com.example.StudentManagement.config.JwtConfig;
import com.example.StudentManagement.dto.request.ChangePasswordRequest;
import com.example.StudentManagement.dto.response.LoginResponse;
import com.example.StudentManagement.dto.request.LoginRequest;
import com.example.StudentManagement.dto.request.RegisterRequest;
import com.example.StudentManagement.dto.response.UserResponse;
import com.example.StudentManagement.entity.RefreshToken;
import com.example.StudentManagement.entity.TokenBlacklist;
import com.example.StudentManagement.entity.User;
import com.example.StudentManagement.exception.DuplicateResourceException;
import com.example.StudentManagement.exception.ResourceNotFoundException;
import com.example.StudentManagement.exception.TokenExpiredException;
import com.example.StudentManagement.exception.WrongPasswordException;
import com.example.StudentManagement.mapper.UserMapper;
import com.example.StudentManagement.repository.RefreshTokenRepository;
import com.example.StudentManagement.repository.RoleRepository;
import com.example.StudentManagement.repository.TokenBlacklistRepository;
import com.example.StudentManagement.repository.UserRepository;
import com.example.StudentManagement.security.JwtUtil;
import com.example.StudentManagement.websocket.RawWebSocketController;
import com.example.StudentManagement.websocket.WebSocketController;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final TokenBlacklistRepository blacklistRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;
    private final RawWebSocketController webSocketController;
    private final RoleRepository roleRepository;



    // Logout user by blacklisting token
    public void logout(String token, Date expiryDate) {
        if (expiryDate == null) expiryDate = new Date();
        String email = jwtUtil.getUsernameFromToken(token);

        //delete refresh token when user logout
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        refreshTokenRepository.deleteByUser(user);

        //store access token into blacklisted
        TokenBlacklist blacklisted = new TokenBlacklist(token, expiryDate.toInstant());
        blacklistRepository.save(blacklisted);
        webSocketController.sendEvent("USER_LOGOUT", userMapper.toDto(user));
    }

    // Check if token is valid and not store in blacklisted
    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token) && !blacklistRepository.existsByToken(token);
    }


    public UserResponse register(RegisterRequest request){
        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByName(name)) {
            throw new DuplicateResourceException("Name is already in use");
        }
        if (userRepository.existsByEmail(email)){
            throw new DuplicateResourceException("Email is already in use");
        }

        var user = userMapper.toEntity(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(email);
        user.setName(name);

        // assign default USER role
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found in DB"));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public LoginResponse login(LoginRequest request){
        String email = request.getEmail().trim().toLowerCase();
        //get from bean authenticationManager in SecurityConfig to verify is email and password correct or not
        //in this process it call loadUserByUsername in UserDetailServiceImpl to check or verify
        //if success continue to generate access and refresh token else return an error exception
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtil.generateToken(email);
        String refreshToken = createRefreshToken(email);
        webSocketController.sendEvent("USER_LOGIN", userMapper.toDto(user));
        return new LoginResponse(accessToken, refreshToken, userMapper.toDto(user));
    }



    public String createRefreshToken(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete old refresh token if exists (single device login)
        refreshTokenRepository.deleteByUser(user);

        String token = java.util.UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtConfig.getRefreshExpiration().toMillis()));
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public LoginResponse refreshAccessToken(String refreshTokenStr) {

        RefreshToken oldToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        //check token is expired or not
        if (oldToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new TokenExpiredException("Refresh token expired");
        }

        User user = oldToken.getUser();

        refreshTokenRepository.delete(oldToken);

        String newRefreshToken = createRefreshToken(user.getEmail());
        String newAccessToken = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(newAccessToken, newRefreshToken, userMapper.toDto(user));
    }

//    public String refreshAccessToken(String refreshTokenStr) {
//        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
//                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
//
//        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
//            refreshTokenRepository.delete(refreshToken);
//            throw new RuntimeException("Refresh token expired");
//        }
//
//        return jwtUtil.generateToken(refreshToken.getUser().getEmail());
//    }



}