package org.example.myphambe.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String address;
    private String phone;
}