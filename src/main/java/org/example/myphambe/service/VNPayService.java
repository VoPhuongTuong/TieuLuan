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

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", config.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(orderId));
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", config.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);

        // ❌ BỎ dòng này
        // params.put("vnp_SecureHashType", "HmacSHA512");

        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        String expireDate = new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime());

        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        boolean first = true;

        for (String key : keys) {
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {

                if (!first) {
                    hashData.append("&");
                    query.append("&");
                }

                String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);

                hashData.append(key).append("=").append(encoded);
                query.append(key).append("=").append(encoded);

                first = false;
            }
        }

        String secureHash = hmacSHA512(config.getSecretKey(), hashData.toString());

        return config.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }
    public boolean verifySignature(Map<String, String> params, String receivedHash) throws Exception {

        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType"); // ✅ cần remove

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder hashData = new StringBuilder();

        boolean first = true;

        for (String key : keys) {
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {

                if (!first) {
                    hashData.append("&");
                }

                String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);

                hashData.append(key).append("=").append(encoded);

                first = false;
            }
        }

        String calcHash = hmacSHA512(config.getSecretKey(), hashData.toString());

        System.out.println("HASH DATA: " + hashData);
        System.out.println("CALC HASH: " + calcHash);
        System.out.println("RECEIVED HASH: " + receivedHash);

        return calcHash.equals(receivedHash);
    }
    private String hmacSHA512(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(secretKeySpec);

        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}