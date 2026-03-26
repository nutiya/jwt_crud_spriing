package com.example.StudentManagement.repository;

import com.example.StudentManagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // find role by name
    boolean existsByName(String name);      // check duplicate
}