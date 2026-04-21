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
    private Integer role; // 1: Admin, 0: User
    private String avatar; // Để map với URL avatar ở frontend
    
    // Bạn có thể thêm các field thống kê nếu cần
    private Long totalOrders;
    private Double totalSpent;
}