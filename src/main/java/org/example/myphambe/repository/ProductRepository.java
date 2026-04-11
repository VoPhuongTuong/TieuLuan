package org.example.myphambe.repository;

import org.example.myphambe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // lấy tất cả sản phẩm theo category
    List<Product> findByCategory_Id(Integer categoryId);

    // lấy 5 sản phẩm đầu theo category
    List<Product> findTop6ByCategory_Id(Integer categoryId);
            // Search theo name, brand, description
        @Query("SELECT p FROM Product p WHERE " +
                "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<Product> searchProducts(@Param("keyword") String keyword);
}