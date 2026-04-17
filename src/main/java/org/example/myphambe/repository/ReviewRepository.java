package org.example.myphambe.repository;

import org.example.myphambe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductId(Integer productId);
    // Tìm review theo ID và User để đảm bảo chính chủ mới được sửa/xóa
    Optional<Review> findByIdAndUser(Integer id, String user);
}