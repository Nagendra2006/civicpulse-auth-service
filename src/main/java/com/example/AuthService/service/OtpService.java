package com.example.AuthService.service;

import com.example.AuthService.client.NotificationClient;
import com.example.AuthService.dto.NotificationRequest;
import com.example.AuthService.entity.OtpVerification;
import com.example.AuthService.repository.OtpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepo;

    @Autowired
    private NotificationClient notificationClient;

    // 🔹 Generate OTP
    public String sendOtp(String email, String purpose) {

        // Generate OTP
        String otpValue = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Save OTP
        OtpVerification otp = new OtpVerification();
        otp.setEmail(email);
        otp.setOtpCode(otpValue);
        otp.setPurpose(purpose); // ✅ use dynamic purpose
        otp.setIsVerified(false);
        otp.setCreatedAt(LocalDateTime.now());

        otpRepo.save(otp);

        // 🔥 Send notification
        NotificationRequest req = new NotificationRequest();
        req.setEventType("OTP_SEND");
        req.setRecipientEmail(email);

        Map<String, Object> data = new HashMap<>();
        data.put("otp", otpValue);

        req.setData(data);

        try {
            notificationClient.sendNotification(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OTP sent successfully";
    }

    public String verifyOtp(String email, String otpValue) {

        OtpVerification otp = otpRepo
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, "REGISTER")
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "OTP not found"));

        if (!otp.getOtpCode().equals(otpValue)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        otp.setIsVerified(true);
        otpRepo.save(otp);

        return "OTP verified";
    }

}