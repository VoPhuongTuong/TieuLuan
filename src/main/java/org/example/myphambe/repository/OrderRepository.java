package org.example.myphambe.repository;

import org.example.myphambe.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Lấy tất cả đơn hàng của 1 user
    List<Order> findByUserId(Integer userId);
}