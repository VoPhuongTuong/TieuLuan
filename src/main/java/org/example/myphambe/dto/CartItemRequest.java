package org.example.myphambe.dto;

import lombok.Data;

@Data
public class CartItemRequest {
 private Integer userId;
    private Integer productId;
    private Integer quantity;
}