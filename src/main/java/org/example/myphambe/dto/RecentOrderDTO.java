package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RecentOrderDTO {
    private String id;
    private String customer;
    private BigDecimal total;
    private String status;
    private String date;
}
