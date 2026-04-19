package org.example.myphambe.repository;

import org.example.myphambe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductAdminRepository extends JpaRepository<Product, Integer> {
    // Admin có thể tìm kiếm theo brand hoặc tên
    List<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);

    // Truy vấn sản phẩm sắp hết hàng (dưới 10 cái)
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < 10")
    List<Product> findLowStockProducts();
}