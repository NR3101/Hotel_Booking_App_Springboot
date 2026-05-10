package com.nr3101.hotelbookingapp.security;

import com.nr3101.hotelbookingapp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service for generating and validating JWT tokens for user authentication.
 */
@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    // Convert the secret key string to a SecretKey object
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Generate a JWT token for the given user
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString()) // Set the subject to the user's ID
                .claim("email", user.getEmail()) // Include email in the token claims
                .claim("roles", user.getRoles().toString()) // Include user roles in the token claims
                .issuedAt(new Date()) // Set the token issue time to now
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
                .signWith(getSecretKey()) // Sign the token with the secret key
                .compact();
    }

    // Generate a refresh token for the given user
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString()) // Set the subject to the user's ID
                .issuedAt(new Date()) // Set the token issue time to now
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 * 6)) // 6 months
                .signWith(getSecretKey()) // Sign the token with the secret key
                .compact();
    }

    // Extract the user ID from the given JWT token
    public Long getUserIdFromToken(String token) {
//        return Long.parseLong(Jwts.parser()
//                .verifyWith(getSecretKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject());

        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }
}
