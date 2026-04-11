package org.example.myphambe.service;

import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Review;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    // Lấy chi tiết sản phẩm + reviews
    public Product getProductDetail(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        List<Review> reviews = reviewRepository.findByProductId(id);
        product.setReviews(reviews);
        return product;
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

    // Lấy 6 sản phẩm nổi bật theo category
    public List<Product> getTop6Products(Integer categoryId) {
        return productRepository.findTop6ByCategory_Id(categoryId);
    }
}