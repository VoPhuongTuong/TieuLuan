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
    java.util.Optional<Order> findById(Integer orderId);

        @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE LOWER(o.status) = LOWER(:status)")
        Double sumRevenueByStatus(@Param("status") String status);

        @Query("SELECT FUNCTION('DATE', o.orderDate), SUM(o.totalPrice) " +
                "FROM Order o " +
                "WHERE o.orderDate >= :startDate " +
                "GROUP BY FUNCTION('DATE', o.orderDate) " +
                "ORDER BY FUNCTION('DATE', o.orderDate) ASC")
        List<Object[]> getRawRevenueLast7Days(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.orderDate DESC")
    List<Order> findTopRecentOrders(Pageable pageable);


    @Query("SELECT o FROM Order o WHERE " +
            "(:status = 'all' OR o.status = :status) AND " +
            "(CAST(o.orderId AS string) LIKE %:search% OR " +
            "LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "o.user.phone LIKE %:search%)")
    List<Order> findAllByAdmin(@Param("status") String status, @Param("search") String search);
}
