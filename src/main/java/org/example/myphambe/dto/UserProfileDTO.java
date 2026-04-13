package org.example.myphambe.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phone;
}