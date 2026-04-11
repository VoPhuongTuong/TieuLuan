package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public User getProfile(Principal principal) {

        String email = principal.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}