package org.example.myphambe.controller;

import org.example.myphambe.entity.Review;
import org.example.myphambe.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "*") // Cho phép frontend call
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/{id}/reviews")
    public List<Review> getReviewsByProduct(@PathVariable Integer id) {
        return reviewRepository.findByProductId(id);
    }
}