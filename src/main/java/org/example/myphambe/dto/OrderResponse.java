package org.example.myphambe.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Integer orderId;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
}