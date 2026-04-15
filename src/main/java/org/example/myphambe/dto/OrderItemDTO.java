package org.example.myphambe.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;
    private ProductDTO product;

}