package com.example.AuthService.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.AuthService.entity.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
        );
    }
    // 🔥 UPDATED TOKEN GENERATION
    public String generateToken(User user) {

        String role = user.getRoles()
                .stream()
                .findFirst()
                .get()
                .getName();

        return Jwts.builder()
                .setSubject(user.getEmail())

                // 🔥 ADD THESE CLAIMS
                .claim("userId", user.getId())
                .claim("role", role)
                .claim("districtId", user.getDistrictId())

                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignKey())
                .compact();
    }

    // 🔹 Extract Username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔹 Extract Claim
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    // 🔹 Extract All Claims (FIXED METHOD)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // ✅ correct for new version
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token) // ✅ works now
                .getBody();
    }

    // 🔹 Validate Token
    public boolean isTokenValid(String token, String email) {
        return extractUsername(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}