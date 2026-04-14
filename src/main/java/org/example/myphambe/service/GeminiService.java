package org.example.myphambe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    // SỬA TẠI ĐÂY: Để nguyên URL gốc bạn vừa test thành công
    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash";
    public String getChatResponse(String userPrompt, String productContext) {
        RestTemplate restTemplate = new RestTemplate();

        // Nối chuỗi cẩn thận: URL + ":generateContent?key=" + KEY
        String fullUrl = BASE_URL + ":generateContent?key=" + apiKey;

        // BẮT BUỘC: Header phải có Content-Type JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String systemInstruction = "Bạn là nhân viên Mỹ phẩm Skinly. Dữ liệu: " + productContext + ". Câu hỏi: " + userPrompt;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", systemInstruction)
                        ))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Log URL ra Console để kiểm tra xem có bị thừa dấu cách không
            System.out.println("Calling URL: " + fullUrl);

            Map<String, Object> response = restTemplate.postForObject(fullUrl, entity, Map.class);

            // Trích xuất kết quả (Giữ nguyên logic cũ)
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            return ((Map) parts.get(0)).get("text").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi thực thi: " + e.getMessage();
        }
    }
}