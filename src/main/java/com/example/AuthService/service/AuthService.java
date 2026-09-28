package com.example.AuthService.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.AuthService.client.NotificationClient;
import com.example.AuthService.dto.AuthResponse;
import com.example.AuthService.dto.CreateOfficerRequest;
import com.example.AuthService.dto.LoginRequest;
import com.example.AuthService.dto.NotificationRequest;
import com.example.AuthService.dto.OfficerResponse;
import com.example.AuthService.dto.RegisterRequest;
import com.example.AuthService.entity.OtpVerification;
import com.example.AuthService.entity.Role;
import com.example.AuthService.entity.User;
import com.example.AuthService.repository.OtpRepository;
import com.example.AuthService.repository.RoleRepository;
import com.example.AuthService.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private OtpRepository otpRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationClient notificationClient;

    public AuthResponse register(RegisterRequest request) {

        // Check if already exists
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "User already exists");
        }
        
        if (userRepo.findByPhone(request.getPhone()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Phone number already registered");
        }

        // STEP 1: Check OTP verification
        OtpVerification otp = otpRepo
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(
                        request.getEmail(), "REGISTER")
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "OTP not found"));

        if (!otp.getIsVerified()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "OTP not verified");
        }

        // STEP 2: Create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // later encrypt
        user.setPhone(request.getPhone());
        user.setDistrictId(request.getDistrictId());
        user.setMandalId(request.getMandalId());
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);

        // STEP 3: Assign role
        Role role = roleRepo.findByName("CITIZEN")
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Role not found"));

        user.addRole(role);

        userRepo.save(user);

        // STEP 4: Generate JWT
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {

        // Fetch user
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Generate JWT
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "Login successful");
    }

    public String createOfficer(CreateOfficerRequest request) {

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Email already exists");
        }
        
        if (userRepo.findByPhone(request.getPhone()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Phone number already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        user.setDepartmentId(request.getDepartmentId());
        user.setMandalId(request.getMandalId());
        user.setDistrictId(request.getDistrictId());

        // 🔥 FETCH ROLE FROM DB
        Role officerRole = roleRepo.findByName("OFFICER")
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Role not found"));

        user.addRole(officerRole);

        userRepo.save(user);

        return "Officer created successfully";
    }

    public List<OfficerResponse> getAllOfficers() {
        return userRepo.findAll()
                .stream()
                .filter(user -> user.getIsActive() != null && user.getIsActive())
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> "OFFICER".equals(role.getName())))
                .map(user -> {
                    OfficerResponse dto = new OfficerResponse();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setMandalId(user.getMandalId());
                    dto.setDepartmentId(user.getDepartmentId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<OfficerResponse> getOfficersByMandal(Long mandalId) {

        return userRepo.findByMandalIdAndIsActiveTrue(mandalId)
                .stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> "OFFICER".equals(role.getName())))
                .map(user -> {
                    OfficerResponse dto = new OfficerResponse();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setMandalId(user.getMandalId());
                    dto.setDepartmentId(user.getDepartmentId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<OfficerResponse> getOfficersByMandalAndDepartment(Long mandalId, Long departmentId) {

        return userRepo
                .findByMandalIdAndDepartmentIdAndIsActiveTrue(mandalId, departmentId)
                .stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(r -> "OFFICER".equals(r.getName())))
                .map(user -> {
                    OfficerResponse dto = new OfficerResponse();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setMandalId(user.getMandalId());
                    dto.setDepartmentId(user.getDepartmentId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
