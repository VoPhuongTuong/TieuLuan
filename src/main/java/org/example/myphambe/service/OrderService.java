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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

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
}