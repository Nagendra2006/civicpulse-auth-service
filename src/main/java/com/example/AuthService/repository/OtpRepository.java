package com.example.AuthService.repository;

import com.example.AuthService.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailAndOtpCodeOrderByCreatedAtDesc(String email, String otpCode);
    Optional<OtpVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, String purpose);
}