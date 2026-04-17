package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.*;
import org.example.myphambe.entity.Order;
import org.example.myphambe.entity.OrderItem;
import org.example.myphambe.entity.Product;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.OrderRepository;
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.myphambe.entity.CartItem;
import org.example.myphambe.repository.CartItemRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository; // Thêm repository này

    public Order createOrder(Integer userId, List<CartItemRequest> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderItem> items = cartItems.stream().map(ci -> {
            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(ci.getQuantity());
            item.setPrice(product.getPrice());

            return item;
        }).toList();

        order.setItems(items);

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }


    public List<OrderResponse> getOrdersByUser(Integer userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(order -> {

            OrderResponse dto = new OrderResponse();
            dto.setOrderId(order.getOrderId());
            dto.setStatus(order.getStatus());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setOrderDate(order.getOrderDate());


            List<OrderItemDTO> items = order.getItems().stream().map(item -> {

                ProductDTO p = new ProductDTO();
                p.setId(item.getProduct().getId());
                p.setName(item.getProduct().getName());
                p.setImageUrl(item.getProduct().getImageUrl());

                OrderItemDTO i = new OrderItemDTO();
                i.setProductId(item.getProduct().getId());
                i.setQuantity(item.getQuantity());
                i.setPrice(item.getPrice());
                i.setProduct(p);

                return i;
            }).toList();

            dto.setItems(items);

            return dto;
        }).toList();
    }

    // 1. Lấy chi tiết 1 đơn hàng
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToOrderResponse(order);
    }

    // 2. Hủy đơn hàng (Chỉ cho phép khi đang PENDING)
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đang chờ xác nhận");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    // 3. Mua lại đơn hàng (Reorder)
    public void reorderToCart(Integer orderId) {
        // 1. Tìm đơn hàng cũ
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        User user = oldOrder.getUser();

        // 2. Duyệt qua từng sản phẩm trong đơn hàng cũ
        for (OrderItem orderItem : oldOrder.getItems()) {
            Product product = orderItem.getProduct();

            // 3. Kiểm tra xem sản phẩm này đã có trong giỏ hàng của user chưa
            Optional<CartItem> existingItem = cartItemRepository
                    .findByUser_IdAndProduct_Id(user.getId(), product.getId());

            if (existingItem.isPresent()) {
                // Nếu đã có, cộng dồn số lượng
                CartItem cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + orderItem.getQuantity());
                cartItemRepository.save(cartItem);
            } else {
                // Nếu chưa có, tạo mới một dòng trong giỏ hàng
                CartItem newCartItem = new CartItem();
                newCartItem.setUser(user);
                newCartItem.setProduct(product);
                newCartItem.setQuantity(orderItem.getQuantity());
                cartItemRepository.save(newCartItem);
            }
        }
    }

    // Helper method để tránh lặp code (Refactor từ hàm getOrdersByUser)
    public OrderResponse mapToOrderResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setOrderId(order.getOrderId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderDate(order.getOrderDate());

        List<OrderItemDTO> items = order.getItems().stream().map(item -> {
            ProductDTO p = new ProductDTO();
            p.setId(item.getProduct().getId());
            p.setName(item.getProduct().getName());
            p.setImageUrl(item.getProduct().getImageUrl());

            OrderItemDTO i = new OrderItemDTO();
            i.setProductId(item.getProduct().getId());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setProduct(p);
            return i;
        }).toList();

        dto.setItems(items);
        return dto;
    }
}