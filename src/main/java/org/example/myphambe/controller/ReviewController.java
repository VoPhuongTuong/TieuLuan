package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Review;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "*") // Cho phép frontend call
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewController(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/{id}/reviews")
    public List<Review> getReviewsByProduct(@PathVariable Integer id) {
        return reviewRepository.findByProductId(id);
    }



    @PostMapping("/{productId}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Integer productId,
            @RequestBody Review reviewRequest) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không thấy sản phẩm"));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(reviewRequest.getUser()); // Tên user gửi từ App
        review.setContent(reviewRequest.getContent());
        review.setStars(reviewRequest.getStars());

        return ResponseEntity.ok(reviewRepository.save(review));
    }


}