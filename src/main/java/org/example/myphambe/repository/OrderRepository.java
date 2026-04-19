package org.example.myphambe.repository;

import org.example.myphambe.dto.RevenueChartDTO;
import org.example.myphambe.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"items", "items.product"})
    java.util.Optional<Order> findById(Integer orderId); // Ghi đè hàm mặc định

        @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE LOWER(o.status) = LOWER(:status)")
        Double sumRevenueByStatus(@Param("status") String status);

        // Trả về List<Object[]> để tránh lỗi Constructor DTO
        // Object[0] sẽ là Date, Object[1] sẽ là Sum
        @Query("SELECT FUNCTION('DATE', o.orderDate), SUM(o.totalPrice) " +
                "FROM Order o " +
                "WHERE o.orderDate >= :startDate " +
                "GROUP BY FUNCTION('DATE', o.orderDate) " +
                "ORDER BY FUNCTION('DATE', o.orderDate) ASC")
        List<Object[]> getRawRevenueLast7Days(@Param("startDate") LocalDateTime startDate);

    // Lấy danh sách đơn hàng mới nhất, kèm thông tin User để hiển thị tên khách hàng
    @Query("SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.orderDate DESC")
    List<Order> findTopRecentOrders(Pageable pageable);


    // Tìm kiếm đơn hàng theo tên User, SĐT hoặc ID cho Admin
    @Query("SELECT o FROM Order o WHERE " +
            "(:status = 'all' OR o.status = :status) AND " +
            "(CAST(o.orderId AS string) LIKE %:search% OR " +
            "LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "o.user.phone LIKE %:search%)")
    List<Order> findAllByAdmin(@Param("status") String status, @Param("search") String search);
}
