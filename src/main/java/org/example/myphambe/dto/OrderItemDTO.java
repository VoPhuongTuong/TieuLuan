package org.example.myphambe.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer productId;
    private Integer quantity;
    private Double price;
}