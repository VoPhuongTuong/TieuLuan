package org.example.myphambe.repository;

import org.example.myphambe.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByUserId(Integer userId);

}