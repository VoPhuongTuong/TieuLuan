package org.example.myphambe.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.LoginResponse;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.UserRepository;
import org.example.myphambe.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Thay bằng Client ID (Web) bạn lấy từ Google Cloud Console
    private final String GOOGLE_CLIENT_ID = "270128411389-puaajt7jbuec792sp5mli4kkg1t78b25.apps.googleusercontent.com";

    public LoginResponse authenticateGoogleUser(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            // Kiểm tra user trong DB
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // Nếu chưa có thì tự động tạo tài khoản mới
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setFullName(fullName);
                        newUser.setRole(1); // Mặc định role là 1 (USER)
                        newUser.setUsername(email); // Username tạm lấy theo email
                        // Password có thể để null hoặc generate ngẫu nhiên vì login qua Google
                        return userRepository.save(newUser);
                    });

            // Tạo Token từ JwtUtil của bạn (sử dụng: id, email, role)
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

            // Trả về đúng format LoginResponse hiện tại của bạn
            return new LoginResponse(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    token
            );
        } else {
            throw new RuntimeException("Xác thực Google ID Token thất bại");
        }
    }
}