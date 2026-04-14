package org.example.myphambe.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.entity.Order;
import org.example.myphambe.repository.OrderRepository;
import org.example.myphambe.service.VNPayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//@RestController
@Controller

@RequestMapping("/api/payment")
@RequiredArgsConstructor
@ResponseBody
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

    /// /        String ip = request.getRemoteAddr();
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

//        String ip = request.getHeader("X-FORWARDED-FOR");
//        if (ip == null || ip.isEmpty()) {
//            ip = request.getRemoteAddr();
//        }
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

// fix localhost IPv6
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }


        // ✅ FIX QUAN TRỌNG: nhân 100
        String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, ip);
        return Map.of("paymentUrl", paymentUrl);
    }
    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request) throws Exception {

        Map<String, String> params = new HashMap<>();

        request.getParameterMap().forEach((k, v) -> {
            if (v.length > 0) params.put(k, v[0]);
        });

        String secureHash = params.get("vnp_SecureHash");
        boolean valid = vnPayService.verifySignature(params, secureHash);
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        // Lấy orderId từ txnRef (VNPAY trả về)
        int orderId = Integer.parseInt(txnRef);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (valid && "00".equals(responseCode)) {
            order.setStatus("PAID");
        } else {
            order.setStatus("FAILED");
        }

        orderRepository.save(order);

        /**
         * ✅ THAY ĐỔI QUAN TRỌNG TẠI ĐÂY:
         * Thay vì redirect về myapp:// (Deeplink), ta trả về nội dung text.
         * Khi WebView load trang này, React Native sẽ thấy URL có
         * vnp_ResponseCode=00 và tự động đóng Modal.
         */
        return "PAYMENT_COMPLETED";
    }
}