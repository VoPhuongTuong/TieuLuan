package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpData {
    private String otp;
    private long expireAt; // millis
}
