package org.example.myphambe.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.entity.Order;
import org.example.myphambe.repository.OrderRepository;
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

    // ===================== CREATE PAYMENT =====================
//    @PostMapping("/vnpay/create")
//    public Map<String, String> createPayment(
//            @RequestParam int orderId,
//            @RequestParam long amount,
//            HttpServletRequest request
//    ) throws Exception {
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
////        String ip = request.getRemoteAddr();
//        String ip = request.getHeader("X-FORWARDED-FOR");
//        if (ip == null || ip.isEmpty()) {
//            ip = request.getRemoteAddr();
//        }
//        String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, ip);
//
//        return Map.of("paymentUrl", paymentUrl);
//    }
    @PostMapping("/vnpay/create")
    public Map<String, String> createPayment(
            @RequestParam int orderId,
            @RequestParam long amount,
            HttpServletRequest request
    ) throws Exception {

        System.out.println("ORDER ID: " + orderId);
        System.out.println("AMOUNT: " + amount);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        // ✅ FIX QUAN TRỌNG: nhân 100
//        String paymentUrl = vnPayService.createPaymentUrl(orderId, amount * 100, ip);
        String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, ip);
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

        boolean valid = vnPayService.verifySignature(params, secureHash);

        if (!valid) {
            return "Invalid signature";
        }

        String responseCode = params.get("vnp_ResponseCode");
        int orderId = Integer.parseInt(params.get("vnp_TxnRef"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("00".equals(responseCode)) {
            order.setStatus("PAID");
        } else {
            order.setStatus("FAILED");
        }

        orderRepository.save(order);

        return responseCode.equals("00")
                ? "Thanh toán thành công"
                : "Thanh toán thất bại";
    }
}