package org.example.myphambe.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderAdminResponseDTO {
    private String id; // Map từ orderId (String để khớp với React Native)
    private Integer rawId; // ID nguyên bản để gọi API update
    private CustomerDTO customer;
    private List<ItemDTO> items;
    private BigDecimal total;
    private String status;
    private String paymentMethod; // Mặc định "COD" nếu database chưa có
    private String paymentStatus; // "paid" hoặc "unpaid" dựa trên status
    private String orderDate;
    private String note;

    @Data
    public static class CustomerDTO {
        private String name;
        private String phone;
        private String address;
        private String email;
    }

    @Data
    public static class ItemDTO {
        private String name;
        private Integer quantity;
        private BigDecimal price;
    }
}
