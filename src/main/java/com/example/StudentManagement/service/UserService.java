package com.example.StudentManagement.service;

import com.example.StudentManagement.dto.request.ChangePasswordRequest;
import com.example.StudentManagement.dto.request.StudentRequest;
import com.example.StudentManagement.dto.request.UpdateUserRequest;
import com.example.StudentManagement.dto.response.StudentResponse;
import com.example.StudentManagement.dto.response.UserResponse;
import com.example.StudentManagement.entity.Role;
import com.example.StudentManagement.entity.User;
import com.example.StudentManagement.exception.DuplicateResourceException;
import com.example.StudentManagement.exception.ResourceNotFoundException;
import com.example.StudentManagement.exception.WrongPasswordException;
import com.example.StudentManagement.mapper.UserMapper;
import com.example.StudentManagement.repository.RoleRepository;
import com.example.StudentManagement.repository.UserRepository;
import com.example.StudentManagement.websocket.WebSocketController;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final WebSocketController webSocketController;




    public void changePassword(Long id, ChangePasswordRequest request){
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new WrongPasswordException("Invalid password");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        webSocketController.sendEvent("USER_PASSWORD_CHANGED", userMapper.toDto(user));
    }

    public List<UserResponse> getAllUsers(String sort, String keyword) {
        // Validate sort field
        if (!Set.of("id", "name", "email").contains(sort.toLowerCase())) {
            sort = "id";
        }

        List<User> users;

        if (keyword != null && !keyword.isBlank()) {
            // Search by keyword (name or email)
            users = userRepository.searchByKeyword(keyword, Sort.by(sort));
        } else {
            // Get all users sorted
            users = userRepository.findAll(Sort.by(sort));
        }

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }


    public UserResponse updateUser(Long id, UpdateUserRequest request){
        String newName = request.getName().trim();
        String newEmail = request.getEmail().trim().toLowerCase();
        var student = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        if(!newEmail.equals(student.getEmail())){
            if(userRepository.existsByEmail(newEmail)){
                throw new DuplicateResourceException("Email is already in use");
            }
        }

        if(!newName.equals(student.getName())){
            if(userRepository.existsByName(newName)){
                throw new DuplicateResourceException("Name is already in use");
            }
        }

        userMapper.update(request, student);
        student.setName(newName);
        student.setEmail(newEmail);
        userRepository.save(student);

        return userMapper.toDto(student);
    }


    public UserResponse updateUserRoles(Long id, Set<Long> roleIds){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        Set<Role> roles = roleRepository.findAllById(roleIds)
                .stream().collect(Collectors.toSet());

        if(roles.size() != roleIds.size()){
            throw new ResourceNotFoundException("Some roles not found");
        }

        user.setRoles(roles);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteStudent(Long id){
        var student = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
        userRepository.delete(student);
    }
}
