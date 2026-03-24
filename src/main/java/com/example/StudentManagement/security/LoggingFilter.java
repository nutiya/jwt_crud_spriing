package com.example.StudentManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);


    //print log in terminal for all request and response
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        //Incoming Request: GET /api/external/random-user from 0:0:0:0:0:0:0:1
        log.info("Incoming Request: {} {} from {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());

        filterChain.doFilter(request, response);

        //Outgoing Response: GET /api/external/random-user - Status: 200 - Duration: 446 ms
        long duration = System.currentTimeMillis() - startTime;
        log.info("Outgoing Response: {} {} - Status: {} - Duration: {} ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
    }
}