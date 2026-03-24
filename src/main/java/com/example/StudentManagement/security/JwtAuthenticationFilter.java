package com.example.StudentManagement.security;

import com.example.StudentManagement.dto.response.ApiResponse;
import com.example.StudentManagement.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;


//filter that checks the JWT on every request
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;//for extracting username from JWT and verifying the token.
    private final UserDetailsServiceImpl userDetailsService;//loads user information from DB.
    private final AuthService authService;//custom service to check if token is valid.
    private final ObjectMapper objectMapper;//to write JSON responses for errors.

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserDetailsServiceImpl userDetailsService,
                                   @Lazy AuthService authService,//avoids circular dependency issues
                                   ObjectMapper objectMapper
                                   ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }


    //this use to do filter for each request. one request one call
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        //get header of request with Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        //if the header not equal Authorization it will store null
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        //check is the header have value and have bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //removes "Bearer " prefix to get only token string
            token = authHeader.substring(7);
            //get username from token
            username = jwtUtil.getUsernameFromToken(token);
        }

        //each request come with context null cause the request not verify yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //get user details from the database using the extracted username.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //validate is the token correct or not
            if (authService.isTokenValid(token)) {
                //after token valid it stores user detail and role permission but not store password.
                //userDetails stores principal(email),
                //null credentials(password),
                //getAuthorities authorities(role or permission)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Mark the current user as authenticated in Spring Security
                // After this, Spring knows who the user is and what roles/permissions they have
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                sendUnauthorizedResponse(response, "You must login first", request.getRequestURI());
                return;
            }
        }

        // Continue the request to the next filter in the chain
        // If this is the last filter, the request reaches the controller
        filterChain.doFilter(request, response);
    }



    //error exception response
    private void sendUnauthorizedResponse(HttpServletResponse response, String message, String path) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                401,
                message,
                null,
                path
        );
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}