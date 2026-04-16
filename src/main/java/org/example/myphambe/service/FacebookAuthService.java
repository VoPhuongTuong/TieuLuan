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
    private final JwtUtil jwtUtil; // Inject JwtUtil thay vì Provider
    private final RestTemplate restTemplate; // Bean này lấy từ SecurityConfig mình bảo bạn thêm ở trên

    public LoginResponse authenticateFacebookUser(String accessToken) {
        // 1. Lấy thông tin từ Facebook Graph API
        String url = "https://graph.facebook.com/me?access_token={token}&fields=id,name,email";
        FacebookUserResponse fbUser = restTemplate.getForObject(url, FacebookUserResponse.class, accessToken);

        if (fbUser == null || fbUser.getId() == null) {
            throw new RuntimeException("Xác thực Facebook thất bại");
        }

        // 2. Kiểm tra hoặc tạo User mới
        String email = fbUser.getEmail() != null ? fbUser.getEmail() : fbUser.getId() + "@facebook.com";

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(fbUser.getName());
                    newUser.setUsername("fb_" + fbUser.getId());
                    newUser.setRole(1); // Integer role khách hàng là 1
                    newUser.setPassword("");
                    return userRepository.save(newUser);
                });

        // 3. Tạo JWT sử dụng đúng hàm generateToken của bạn
        // Hàm của bạn: generateToken(Integer id, String email, Integer role)
        String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        // 4. Trả về LoginResponse chuẩn của bạn
        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                jwt
        );
    }
}