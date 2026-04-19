package org.example.myphambe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private double totalRevenue;
    private long totalOrders;
    private long totalProducts;
    private long totalUsers;
    private List<RevenueChartDTO> chartData;
    private List<RecentOrderDTO> recentOrders; // Thêm dòng này
}