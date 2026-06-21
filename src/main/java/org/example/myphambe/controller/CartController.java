package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.CartItemRequest;
import org.example.myphambe.dto.CartItemResponse;
import org.example.myphambe.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public List<CartItemResponse> getCart(@PathVariable Integer userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<String> addToCart(@PathVariable Integer userId, @RequestBody CartItemRequest req) {
        try {
            req.setUserId(userId);
            cartService.addToCart(req);
            return ResponseEntity.ok("Added to cart");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server");
        }
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Integer userId,
            @RequestBody CartItemRequest req) {
        try {
            req.setUserId(userId);
            cartService.updateQuantity(req);
            return ResponseEntity.ok("Updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        cartService.removeItem(userId, productId);
        System.out.println("DELETE productId = " + productId);
        System.out.println("DELETE userId = " + userId);
        return ResponseEntity.ok("Removed");
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }
}