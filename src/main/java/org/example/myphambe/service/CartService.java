package org.example.myphambe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.CartItemRequest;
import org.example.myphambe.dto.CartItemResponse;
import org.example.myphambe.entity.CartItem;
import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.CartItemRepository;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    /* ===== LẤY GIỎ HÀNG ===== */
    public List<CartItemResponse> getCart(Integer userId) {
        return cartRepo.findByUser_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ===== THÊM VÀO GIỎ ===== */
    public void addToCart(CartItemRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < req.getQuantity()) {
            throw new RuntimeException("out of stock");
        }
        // giảm stock ngay
        product.setStockQuantity(product.getStockQuantity() - req.getQuantity());
        productRepo.save(product);
        // kiểm tra đã có chưa
        CartItem existing = cartRepo
                .findByUser_IdAndProduct_Id(req.getUserId(), req.getProductId())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
            cartRepo.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(req.getQuantity());
            item.setAddedAt(LocalDateTime.now());

            cartRepo.save(item);
        }
    }

    /* ===== MAP DTO ===== */
    private CartItemResponse mapToResponse(CartItem item) {
        CartItemResponse res = new CartItemResponse();
        res.setId(item.getProduct().getId());
        res.setName(item.getProduct().getName());
        res.setBrand(item.getProduct().getBrand());
        res.setPrice(item.getProduct().getPrice());
        res.setImageUrl(item.getProduct().getImageUrl());
        res.setQuantity(item.getQuantity());
        return res;
    }
}