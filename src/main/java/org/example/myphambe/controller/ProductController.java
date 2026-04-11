package org.example.myphambe.controller;

import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Review;
import org.example.myphambe.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Lấy tất cả sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getProducts(null);
    }

    // Lấy sản phẩm theo category
    @GetMapping("/category/{id}")
    public List<Product> getByCategory(@PathVariable Integer id) {
        return productService.getProductsByCategory(id);
    }
    // Lấy 5 sản phẩm cho HomeScreen
    @GetMapping("/category/{id}/top6")
    public List<Product> getTop6(@PathVariable Integer id) { return productService.getTop6Products(id); }
    // Lấy sản phẩm chi tiết theo id
    @GetMapping("/{id}")
    public Product getProductDetail(@PathVariable Integer id) {
        return productService.getProductDetail(id);
    }

    // Tìm kiếm sản phẩm theo keyword
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String keyword) {
        return productService.getProducts(keyword);
    }


}