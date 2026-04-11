package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.CartItemRequest;
import org.example.myphambe.dto.OrderRequest;
import org.example.myphambe.entity.Order;
import org.example.myphambe.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

//    @PostMapping
//
//    public O
//    rder createOrder(@RequestBody OrderRequest request) {
//        return orderService.createOrder(request);
//    }
    @PostMapping("/create/{userId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable Integer userId,
            @RequestBody List<CartItemRequest> cartItems) {
        Order order = orderService.createOrder(userId, cartItems);
        return ResponseEntity.ok(order);
    }

}