package org.example.myphambe.dto;

import lombok.Data;

@Data
public class OrderItemDTO {   // <- public
    private Integer productId;
    private Integer quantity;
}