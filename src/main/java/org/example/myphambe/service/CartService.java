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

    /* ================= GET CART ================= */
    public List<CartItemResponse> getCart(Integer userId) {
        return cartRepo.findByUser_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ================= ADD TO CART ================= */
    public void addToCart(CartItemRequest req) {

        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be > 0");
        }

        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // chỉ check stock (KHÔNG trừ)
        if (product.getStockQuantity() < req.getQuantity()) {
            throw new RuntimeException("Out of stock");
        }

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

    /* ================= UPDATE QUANTITY ================= */
    public void updateQuantity(CartItemRequest req) {

        if (req.getQuantity() == null || req.getQuantity() < 0) {
            throw new RuntimeException("Invalid quantity");
        }

        CartItem item = cartRepo
                .findByUser_IdAndProduct_Id(req.getUserId(), req.getProductId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // nếu = 0 → xoá luôn
        if (req.getQuantity() == 0) {
            cartRepo.delete(item);
            return;
        }

        // check stock (KHÔNG trừ)
        Product product = item.getProduct();
        if (product.getStockQuantity() < req.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        item.setQuantity(req.getQuantity());
        cartRepo.save(item);
    }

    /* ================= REMOVE ITEM ================= */
    public void removeItem(Integer userId, Integer productId) {

        CartItem item = cartRepo
                .findByUser_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        cartRepo.delete(item);
    }

    /* ================= CLEAR CART ================= */
    public void clearCart(Integer userId) {
        List<CartItem> items = cartRepo.findByUser_Id(userId);
        cartRepo.deleteAll(items);
    }

    /* ================= MAP DTO ================= */
    private CartItemResponse mapToResponse(CartItem item) {
        CartItemResponse res = new CartItemResponse();
        res.setProductId(item.getProduct().getId());
        res.setName(item.getProduct().getName());
        res.setBrand(item.getProduct().getBrand());
        res.setPrice(item.getProduct().getPrice());
        res.setImageUrl(item.getProduct().getImageUrl());
        res.setQuantity(item.getQuantity());
        return res;
    }

}