package org.example.myphambe.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Integer userId;
    private List<OrderItemDTO> items;
    private String paymentMethod;
    private String address;
}