package org.example.myphambe.dto;
import lombok.Data;
@Data
public class UserDTO {
    private Integer id;
    private String userName;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private Integer role;
    private String avatar;
    
    private Long totalOrders;
    private Double totalSpent;
}