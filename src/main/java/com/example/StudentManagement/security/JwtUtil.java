package com.example.StudentManagement.security;

import com.example.StudentManagement.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@AllArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }


    //generate Token key for access token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration().toMillis()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //use to fetch username of user from token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //validate access token correct or not
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    //get expired date of access token
    public Date getExpirationDate(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}