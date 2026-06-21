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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Order createOrder(Integer userId, List<CartItemRequest> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentMethod("COD");
        order.setPaymentStatus("UNPAID");
        List<OrderItem> items = cartItems.stream().map(ci -> {
            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(ci.getQuantity());
            item.setPrice(ci.getPrice() != null ? ci.getPrice() : product.getPrice());
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

    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToOrderResponse(order);
    }

    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đang chờ xác nhận");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    public void reorderToCart(Integer orderId) {
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        User user = oldOrder.getUser();

        for (OrderItem orderItem : oldOrder.getItems()) {
            Product product = orderItem.getProduct();

            Optional<CartItem> existingItem = cartItemRepository
                    .findByUser_IdAndProduct_Id(user.getId(), product.getId());

            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + orderItem.getQuantity());
                cartItemRepository.save(cartItem);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setUser(user);
                newCartItem.setProduct(product);
                newCartItem.setQuantity(orderItem.getQuantity());
                cartItemRepository.save(newCartItem);
            }
        }
    }


    public OrderResponse mapToOrderResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setOrderId(order.getOrderId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderDate(order.getOrderDate());

        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD");
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus() : "UNPAID");

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

    @Transactional(readOnly = true)
    public List<OrderAdminResponseDTO> getAllOrdersForAdmin(String status, String search) {
        List<Order> orders = orderRepository.findAllByAdmin(status, search);
        return orders.stream().map(this::mapToAdminResponse).toList();
    }

    public OrderAdminResponseDTO updateStatusByAdmin(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String statusUpper = newStatus.toUpperCase();
        order.setStatus(statusUpper);

        if ("COD".equalsIgnoreCase(order.getPaymentMethod()) && "DELIVERED".equals(statusUpper)) {
            order.setPaymentStatus("PAID");
        }

        Order saved = orderRepository.save(order);
        return mapToAdminResponse(saved);
    }

    private OrderAdminResponseDTO mapToAdminResponse(Order order) {
        OrderAdminResponseDTO dto = new OrderAdminResponseDTO();

        dto.setId("ORD" + String.format("%03d", order.getOrderId()));
        dto.setRawId(order.getOrderId());
        dto.setTotal(order.getTotalPrice());
        dto.setStatus(order.getStatus() != null ? order.getStatus().toUpperCase() : "PENDING");
        dto.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().format(DATE_FORMATTER) : "");

        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().toUpperCase() : "COD");
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().toUpperCase() : "UNPAID");

        dto.setNote("Giao hàng giờ hành chính");

        if (order.getUser() != null) {
            OrderAdminResponseDTO.CustomerDTO cust = new OrderAdminResponseDTO.CustomerDTO();
            cust.setName(order.getUser().getFullName());
            cust.setPhone(order.getUser().getPhone());
            cust.setAddress(order.getUser().getAddress());
            cust.setEmail(order.getUser().getEmail());
            dto.setCustomer(cust);
        }

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(i -> {
                OrderAdminResponseDTO.ItemDTO itemDto = new OrderAdminResponseDTO.ItemDTO();
                itemDto.setName(i.getProduct() != null ? i.getProduct().getName() : "Sản phẩm đã xóa");
                itemDto.setQuantity(i.getQuantity());
                itemDto.setPrice(i.getPrice());
                return itemDto;
            }).toList());
        }
        return dto;
    }
}