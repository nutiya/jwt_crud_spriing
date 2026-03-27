package com.example.StudentManagement.service;

import com.example.StudentManagement.dto.request.RoleRequest;
import com.example.StudentManagement.dto.response.RoleResponse;
import com.example.StudentManagement.entity.Role;
import com.example.StudentManagement.exception.DuplicateResourceException;
import com.example.StudentManagement.exception.ResourceNotFoundException;
import com.example.StudentManagement.mapper.RoleMapper;
import com.example.StudentManagement.repository.RoleRepository;
import com.example.StudentManagement.websocket.RawWebSocketController;
import com.example.StudentManagement.websocket.WebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RawWebSocketController webSocketController;

    // Create new role
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role already exists");
        }
        Role role = roleMapper.toEntity(request);
        Role saved = roleRepository.save(role);
        webSocketController.sendEvent("ROLE_CREATED", roleMapper.toDto(saved));
        return roleMapper.toDto(saved);
    }

    // Get all roles
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    // Get role by id
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        return roleMapper.toDto(role);
    }

    // Update role
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if(!request.getName().equals(role.getName())){
            if(roleRepository.existsByName(request.getName())){
                throw new DuplicateResourceException("Name is already in use");
            }
        }
        role.setName(request.getName());
        Role updated = roleRepository.save(role);
        webSocketController.sendEvent("ROLE_UPDATED", roleMapper.toDto(updated));
        return roleMapper.toDto(updated);
    }

    // Delete role
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        roleRepository.delete(role);
        webSocketController.sendEvent("ROLE_DELETED", roleMapper.toDto(role));
    }
}