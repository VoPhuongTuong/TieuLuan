package org.example.myphambe.service;

import org.example.myphambe.dto.DashboardStatsDTO;
import org.example.myphambe.dto.RecentOrderDTO;
import org.example.myphambe.dto.RevenueChartDTO;
import org.example.myphambe.entity.Order;
import org.example.myphambe.repository.OrderRepository;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable; // Sửa lại cái này
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class DashboardService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    public DashboardStatsDTO getStats() {
        // 1. Tính tổng doanh thu (Chỉ tính đơn đã giao - 'delivered')
        Double totalRevenue = orderRepository.sumRevenueByStatus("delivered");

        // 2. Đếm tổng số lượng
        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long totalUsers = userRepository.count();
        List<RecentOrderDTO> recentOrders = this.getRecentOrders();
        return new DashboardStatsDTO(
                totalRevenue != null ? totalRevenue : 0.0,
                totalOrders,
                totalProducts,
                totalUsers,
                getChartData(),
                recentOrders // Trả về thêm list này
        );
    }
    private List<RevenueChartDTO> getChartData() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0);
        List<Object[]> rawData = orderRepository.getRawRevenueLast7Days(sevenDaysAgo);

        // Chuyển đổi Object[] sang Map<Ngày, Doanh thu>
        Map<String, Double> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(), // Ngày (yyyy-MM-dd)
                        row -> {
                            // Ép kiểu an toàn từ BigDecimal sang Double
                            Object value = row[1];
                            return (value instanceof BigDecimal) ? ((BigDecimal) value).doubleValue() : (Double) value;
                        },
                        (v1, v2) -> v1 // Nếu trùng ngày thì giữ giá trị đầu
                ));

        List<RevenueChartDTO> filledData = new ArrayList<>();
        // Điền dữ liệu cho 7 ngày gần nhất
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateKey = date.toString(); // "yyyy-MM-dd"
            String label = date.format(DateTimeFormatter.ofPattern("dd/MM")); // "15/04"

            filledData.add(new RevenueChartDTO(label, dataMap.getOrDefault(dateKey, 0.0)));
        }
        return filledData;
    }

    public List<RecentOrderDTO> getRecentOrders() {
        // Sử dụng org.springframework.data.domain.PageRequest
        Pageable topFive = PageRequest.of(0, 5);
        List<Order> orders = orderRepository.findTopRecentOrders(topFive);

        return orders.stream().map(o -> new RecentOrderDTO(
                "ORD" + String.format("%03d", o.getOrderId()), // Format thành ORD001
                o.getUser() != null ? o.getUser().getFullName() : "Khách vãng lai",
                o.getTotalPrice(),
                o.getStatus(),
                o.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )).collect(Collectors.toList());
    }


}