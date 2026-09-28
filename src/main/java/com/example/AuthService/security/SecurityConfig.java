package com.example.AuthService.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                // 🔥 DISABLE CORS (Handled by Gateway)
                                .cors(cors -> cors.disable())

                                .authorizeHttpRequests(auth -> auth

                                                // 🔥 VERY IMPORTANT FOR PREFLIGHT
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // 🔥 PUBLIC APIs
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/error")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}