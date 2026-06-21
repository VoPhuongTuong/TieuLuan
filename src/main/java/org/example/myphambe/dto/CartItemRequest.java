package org.example.myphambe.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemRequest {
 private Integer userId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;
}