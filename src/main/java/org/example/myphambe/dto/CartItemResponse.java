package org.example.myphambe.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Integer id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String imageUrl;
    private Integer quantity;
}