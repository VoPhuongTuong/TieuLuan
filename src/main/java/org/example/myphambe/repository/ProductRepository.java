package org.example.myphambe.repository;

import org.example.myphambe.entity.Product; // Thay bằng đường dẫn Entity của bạn
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    //Phan trang

    // Phân trang cho tất cả sản phẩm
    Page<Product> findAll(Pageable pageable);

    // Phân trang theo category
    Page<Product> findByCategory_Id(Integer categoryId, Pageable pageable);

    // Phân trang tìm kiếm
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    // lấy tất cả sản phẩm theo category
    List<Product> findByCategory_Id(Integer categoryId);

    // lấy 5 sản phẩm đầu theo category
    List<Product> findTop6ByCategory_Id(Integer categoryId);
//     Search theo name, brand, description
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<Product> searchProducts(@Param("keyword") String keyword);

    List<Product> searchProducts(@Param("keyword") String keyword);

}