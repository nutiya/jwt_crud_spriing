package com.example.StudentManagement.controller;


import com.example.StudentManagement.dto.request.ChangePasswordRequest;
import com.example.StudentManagement.dto.request.RoleUpdateRequest;
import com.example.StudentManagement.dto.request.UpdateUserRequest;
import com.example.StudentManagement.dto.response.ApiResponse;
import com.example.StudentManagement.dto.response.UserResponse;
import com.example.StudentManagement.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController extends BaseController{
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll(
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "search", required = false) String search
    ) {
        var users = userService.getAllUsers(sortBy, search);
        return ok(users, "Students fetched successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateUserRoles(id, request.getRoleIds());
        return ok(updatedUser, "User roles updated successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ok(updatedUser, "User updated successfully");
    }


    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id, @Valid ChangePasswordRequest request){
        userService.changePassword(id, request);
        return ok((Void)null, "Change password successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        userService.deleteStudent(id);
        return ok((Void) null, "Student deleted successfully");
    }
}
