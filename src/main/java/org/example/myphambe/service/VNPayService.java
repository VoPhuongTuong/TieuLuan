package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.config.VNPayConfig;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig config;

    public String createPaymentUrl(int orderId, long amount, String ipAddress) throws Exception {

        Map<String, String> params = new HashMap<>();

        // ===== FIX IP =====
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = "127.0.0.1";
        }
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
            ipAddress = "127.0.0.1";
        }

        // ===== BASIC PARAMS =====
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", config.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu *100
        params.put("vnp_CurrCode", "VND");

        // ===== UNIQUE TXN REF =====
//        String txnRef = orderId + "_" + UUID.randomUUID();
        String txnRef = String.valueOf(orderId);
        params.put("vnp_TxnRef", txnRef);

        params.put("vnp_OrderInfo", "Thanh_toan_don_hang_" + orderId);
        params.put("vnp_OrderType", "billpayment");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", config.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);

        // ===== TIME =====
        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        String expireDate = new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime());

        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        // ===== BUILD QUERY + HASH =====
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        boolean first = true;

        for (String key : keys) {
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {

                // encode chuẩn VNPay
                String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                        .replace("+", "%20");

                if (!first) {
                    hashData.append("&");
                    query.append("&");
                }

                hashData.append(key).append("=").append(encoded);
                query.append(key).append("=").append(encoded);

                first = false;
            }
        }

        // ===== HASH =====
        String secureHash = hmacSHA512(config.getSecretKey(), hashData.toString());

        String paymentUrl = config.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;

        // ===== LOG DEBUG =====
        System.out.println("========== VNPAY DEBUG ==========");
        System.out.println("ORDER ID: " + orderId);
        System.out.println("TXN REF: " + txnRef);
        System.out.println("HASH DATA: " + hashData);
        System.out.println("SECURE HASH: " + secureHash);
        System.out.println("FULL PAYMENT URL: " + paymentUrl);
        System.out.println("=================================");

        return paymentUrl;
    }

    public boolean verifySignature(Map<String, String> params, String receivedHash) throws Exception {

        // remove hash fields
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder hashData = new StringBuilder();
        boolean first = true;

        for (String key : keys) {
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {

                String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                        .replace("+", "%20");

                if (!first) {
                    hashData.append("&");
                }

                hashData.append(key).append("=").append(encoded);
                first = false;
            }
        }

        String calcHash = hmacSHA512(config.getSecretKey(), hashData.toString());

        // ===== LOG =====
        System.out.println("========== VERIFY DEBUG ==========");
        System.out.println("HASH DATA: " + hashData);
        System.out.println("CALC HASH: " + calcHash);
        System.out.println("RECEIVED HASH: " + receivedHash);
        System.out.println("=================================");

        return calcHash.equalsIgnoreCase(receivedHash);
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(secretKey);

        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }

        return hash.toString();
    }
}