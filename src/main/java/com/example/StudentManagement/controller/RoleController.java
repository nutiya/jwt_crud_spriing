package com.example.StudentManagement.controller;

import com.example.StudentManagement.dto.request.RoleRequest;
import com.example.StudentManagement.dto.response.RoleResponse;
import com.example.StudentManagement.dto.response.ApiResponse;
import com.example.StudentManagement.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private final RoleService roleService;


    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody RoleRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        RoleResponse response = roleService.createRole(request);

        // Build the location URI dynamically
        URI location = uriBuilder
                .path("/api/roles/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return created(response, "Role created successfully", location);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ok(roles, "All roles fetched");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);
        return ok(response, "Role fetched");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.updateRole(id, request);
        return ok(response, "Role updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ok((Void)null, "Role deleted successfully");
    }
}