package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.FacebookUserResponse;
import org.example.myphambe.dto.LoginResponse;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.UserRepository;
import org.example.myphambe.security.JwtUtil; // Sử dụng JwtUtil của bạn
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FacebookAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    public LoginResponse authenticateFacebookUser(String accessToken) {
        String url = "https://graph.facebook.com/me?access_token={token}&fields=id,name,email";
        FacebookUserResponse fbUser = restTemplate.getForObject(url, FacebookUserResponse.class, accessToken);

        if (fbUser == null || fbUser.getId() == null) {
            throw new RuntimeException("Xác thực Facebook thất bại");
        }

        String email = fbUser.getEmail() != null ? fbUser.getEmail() : fbUser.getId() + "@facebook.com";

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(fbUser.getName());
                    newUser.setUserName("fb_" + fbUser.getId());
                    newUser.setRole(1);
                    newUser.setPassword("");
                    return userRepository.save(newUser);
                });

        String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                jwt
        );
    }
}