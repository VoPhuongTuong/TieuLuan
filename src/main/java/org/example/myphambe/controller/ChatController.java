package org.example.myphambe.controller;

import org.example.myphambe.entity.Product; // Đảm bảo đúng package entity
import org.example.myphambe.repository.ProductRepository;
import org.example.myphambe.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/send")
    public Map<String, String> chatWithAI(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");

        List<Product> products = productRepository.findAll();
        String productContext = products.stream()
                .map(p -> String.format("Sản phẩm: %s | Thương hiệu: %s | Giá: %s VNĐ | Mô tả: %s | Còn hàng: %d",
                        p.getName(), p.getBrand(), p.getPrice().toString(), p.getDescription(), p.getStockQuantity()))
                .collect(Collectors.joining("\n"));

        String aiResponse = geminiService.getChatResponse(userMessage, productContext);

        return Map.of("reply", aiResponse);
    }
}