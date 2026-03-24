package com.example.StudentManagement.security;

import com.example.StudentManagement.entity.User;
import com.example.StudentManagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    //UserDetailsService.loadUserByUsername() → query DB
    //Spring compares passwords
    //Success → return email, password, role and permission
    //Fail → throw exception (UsernameNotFoundException)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Treat username as email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}