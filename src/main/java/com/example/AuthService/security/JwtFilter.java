package com.example.AuthService.security;

import com.example.AuthService.service.JwtService;
// import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 🔹 Check if token exists
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                // 🔹 Extract email
                String email = jwtService.extractUsername(token);

                // 🔹 Extract role
                String role = jwtService.extractClaim(
                        token,
                        claims -> claims.get("role", String.class));

                // 🔹 Set authentication in context
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception e) {
                // Token invalid or expired
                System.out.println("JWT Error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}