package org.example.myphambe.service;

import org.example.myphambe.dto.ReviewRequest;
import org.example.myphambe.dto.ReviewResponse;
import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.Review;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByProduct(Integer productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse addReview(Integer productId, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(request.getUser());
        review.setContent(request.getContent());
        review.setStars(request.getStars());

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    public ReviewResponse updateReview(Integer reviewId, ReviewRequest request) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận này"));

        existingReview.setContent(request.getContent());
        existingReview.setStars(request.getStars());

        Review updatedReview = reviewRepository.save(existingReview);
        return mapToResponse(updatedReview);
    }

    public void deleteReview(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Không tìm thấy bình luận để xóa");
        }
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .user(review.getUser())
                .content(review.getContent())
                .stars(review.getStars())
                .productId(review.getProduct().getId())
                .build();
    }
}