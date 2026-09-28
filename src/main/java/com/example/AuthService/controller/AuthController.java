package com.example.AuthService.controller;

import com.example.AuthService.dto.AuthResponse;
import com.example.AuthService.dto.CreateOfficerRequest;
import com.example.AuthService.dto.LoginRequest;
import com.example.AuthService.dto.OfficerResponse;
import com.example.AuthService.dto.RegisterRequest;
import com.example.AuthService.dto.UserResponse;
import com.example.AuthService.entity.User;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.AuthService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository usersRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    // 🔹 REGISTER
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // 🔹 LOGIN
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-officer")
    public String createOfficer(@RequestBody CreateOfficerRequest request) {
        return authService.createOfficer(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/officers")
    public List<OfficerResponse> getOfficers(@RequestParam(required = false) Long mandalId) {
        if (mandalId == null) {
            return authService.getAllOfficers();
        }
        return authService.getOfficersByMandal(mandalId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/officers/filter")
    public List<OfficerResponse> getFilteredOfficers(
            @RequestParam Long mandalId,
            @RequestParam Long departmentId) {

        return authService.getOfficersByMandalAndDepartment(mandalId, departmentId);
    }

    @GetMapping("/users/{id}")
    public UserResponse getUser(@PathVariable Long id) {

        User user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());

        return res;
    }

}