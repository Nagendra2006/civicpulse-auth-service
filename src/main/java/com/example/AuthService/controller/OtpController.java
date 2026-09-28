package com.example.AuthService.controller;

import com.example.AuthService.dto.OtpRequest;
import com.example.AuthService.dto.OtpVerifyRequest;
import com.example.AuthService.service.OtpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    // 🔹 Send OTP
    @PostMapping("/send")
    public String sendOtp(@Valid @RequestBody OtpRequest request) {

        return otpService.sendOtp(
                request.getEmail(),
                request.getPurpose());
    }

    // 🔹 Verify OTP
    @PostMapping("/verify")
    public String verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {

        return otpService.verifyOtp(
                request.getEmail(),
                request.getOtp());
    }
}