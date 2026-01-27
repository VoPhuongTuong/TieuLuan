package org.example.myphambe.dto;

import lombok.Data;

// dto/VerifyOtpRequest.java
@Data
public class VerifyOtpRequest {
    private String email;
    private String otp;
}

