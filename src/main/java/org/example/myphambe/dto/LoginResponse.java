package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String fullName;
    private String email;
    private Integer role;
    private String token; // 👈 JWT
}
