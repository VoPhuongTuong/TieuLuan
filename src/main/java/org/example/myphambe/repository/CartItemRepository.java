package org.example.myphambe.repository;

import org.example.myphambe.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser_Id(Integer userId);
    Optional<CartItem> findByUser_IdAndProduct_Id(Integer userId, Integer productId);

}