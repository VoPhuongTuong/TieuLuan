package org.example.myphambe.service;

import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Review;
import org.example.myphambe.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    // Lấy chi tiết sản phẩm (Chỉ lấy thông tin cơ bản của sản phẩm)
    public Product getProductDetail(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // Lấy tất cả sản phẩm hoặc tìm kiếm
    public List<Product> getProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.searchProducts(keyword);
    }

    // Lấy sản phẩm theo category
    public List<Product> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    // Lấy 6 sản phẩm nổi bật
    public List<Product> getTop6Products(Integer categoryId) {
        return productRepository.findTop6ByCategory_Id(categoryId);
    }
}