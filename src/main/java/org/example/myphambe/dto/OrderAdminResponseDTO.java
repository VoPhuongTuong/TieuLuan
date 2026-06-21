package org.example.myphambe.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderAdminResponseDTO {
    private String id;
    private Integer rawId;
    private CustomerDTO customer;
    private List<ItemDTO> items;
    private BigDecimal total;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
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
