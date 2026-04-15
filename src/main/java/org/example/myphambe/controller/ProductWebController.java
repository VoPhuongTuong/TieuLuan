package org.example.myphambe.controller;

import org.example.myphambe.entity.Product;
import org.example.myphambe.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/api/QR")

public class ProductWebController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/product/{id}")
    public String getProductPage(@PathVariable Integer id, Model model) {

        Product product = productRepository.findById(id).orElse(null);

        model.addAttribute("product", product);

        return "product"; // trả về product.html
    }
}