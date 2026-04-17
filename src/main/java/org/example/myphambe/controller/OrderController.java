package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.CartItemRequest;
import org.example.myphambe.dto.OrderRequest;
import org.example.myphambe.dto.OrderResponse;
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

    @PostMapping("/create/{userId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable Integer userId,
            @RequestBody List<CartItemRequest> cartItems) {
        Order order = orderService.createOrder(userId, cartItems);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    //    // Lấy chi tiết đơn hàng
//    @GetMapping("/{orderId}")
//    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
//        return ResponseEntity.ok(orderService.getOrderById(orderId));
//    }
// Trong OrderService.java
// Sửa lại đoạn này trong OrderController.java
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // Hủy đơn hàng
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Đã hủy đơn hàng thành công");
    }

//    @PostMapping("/create/{userId}")
//    public ResponseEntity<OrderResponse> createOrder(...) {
//        Order order = orderService.createOrder(userId, cartItems);
//        // Chuyển sang Response DTO trước khi trả về
//        return ResponseEntity.ok(orderService.mapToOrderResponse(order));
//    }

    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<String> reorder(@PathVariable Integer orderId) {
        orderService.reorderToCart(orderId);
        return ResponseEntity.ok("Đã thêm các sản phẩm từ đơn hàng cũ vào giỏ hàng!");
    }
}
