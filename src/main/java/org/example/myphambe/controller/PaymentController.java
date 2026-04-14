package org.example.myphambe.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.entity.Order;
import org.example.myphambe.repository.CartItemRepository;
//import org.example.myphambe.repository.CartRepository;
import org.example.myphambe.repository.OrderRepository;
import org.example.myphambe.service.CartService;
import org.example.myphambe.service.VNPayService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderRepository orderRepository;
    private final VNPayService vnPayService;
    private final CartItemRepository cartRepository;
    private final CartService cartService;
    // ✅ thêm

    // ===================== CREATE PAYMENT =====================
    @PostMapping("/vnpay/create")
    public Map<String, String> createPayment(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) throws Exception {

        int orderId = (int) body.get("orderId");
        long amount = Long.parseLong(body.get("amount").toString());

        System.out.println("ORDER ID: " + orderId);
        System.out.println("AMOUNT: " + amount);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Lấy IP chuẩn
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        // fix localhost IPv6
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        // ⚠️ QUAN TRỌNG: nhân 100 tại đây nếu frontend chưa làm
        long finalAmount = amount * 100;

        String paymentUrl = vnPayService.createPaymentUrl(orderId, finalAmount, ip);

        return Map.of("paymentUrl", paymentUrl);
    }

    // ===================== RETURN =====================
    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request) throws Exception {

        Map<String, String> params = new HashMap<>();

        request.getParameterMap().forEach((k, v) -> {
            if (v.length > 0) params.put(k, v[0]);
        });

        String secureHash = params.get("vnp_SecureHash");

        // ❌ remove hash trước khi verify
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        boolean valid = vnPayService.verifySignature(params, secureHash);

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        int orderId = Integer.parseInt(txnRef);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🚨 FIX QUAN TRỌNG: check chữ ký trước
        if (!valid) {
            order.setStatus("FAILED");
            orderRepository.save(order);
            return "INVALID_SIGNATURE";
        }

        // ✅ Xử lý kết quả thanh toán
        if ("00".equals(responseCode)) {
            order.setStatus("PAID");

            // ✅ XÓA CART TẠI BACKEND (QUAN TRỌNG)
//            cartRepository.deleteByUserId(order.getUser().getId());
            cartService.clearCart(order.getUser().getId());

        } else {
            order.setStatus("FAILED");
        }

        orderRepository.save(order);

        return "PAYMENT_COMPLETED";
    }
}
