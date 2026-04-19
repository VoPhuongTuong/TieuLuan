package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueChartDTO {
    private String label; // Ví dụ: "2024-01-15" hoặc "Thứ 2"
    private Double value; // Doanh thu của ngày đó
}