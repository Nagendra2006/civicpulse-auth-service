package com.example.AuthService.dto;

public class OtpRequest {
    private String email;
    private String purpose;

    public OtpRequest() {}

    public OtpRequest(String email, String purpose) {
        this.email = email;
        this.purpose = purpose;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}