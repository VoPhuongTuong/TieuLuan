package org.example.myphambe.repository;

import org.example.myphambe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Lấy tất cả review của một sản phẩm theo product.id
    List<Review> findByProductId(Integer productId);
}