package org.example.myphambe.controller;

import org.example.myphambe.dto.ReviewRequest;
import org.example.myphambe.dto.ReviewResponse;
import org.example.myphambe.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Integer productId,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(productId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Integer id,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}