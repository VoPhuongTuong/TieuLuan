package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.myphambe.dto.*;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.UserRepository;
import org.example.myphambe.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;


    // LƯU TẠM
    private final Map<String, RegisterRequest> pendingUsers = new ConcurrentHashMap<>();
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, OtpData> resetOtpStorage = new ConcurrentHashMap<>();


    // ================= REGISTER =================
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        String otp = String.valueOf(
                new Random().nextInt(900000) + 100000
        );
        long expireAt = System.currentTimeMillis() + 60_000; // ⏰ 1 phút

        pendingUsers.put(request.getEmail(), request);
        otpStorage.put(request.getEmail(), new OtpData(otp, expireAt));

        sendOtpEmail(request.getEmail(), otp);

        log.info("REGISTER OK email={}, otp={}", request.getEmail(), otp);
    }

    // ================= VERIFY OTP =================
    // ================= VERIFY OTP =================
//    public void verifyOtp(VerifyOtpRequest request) {
//
//        String email = request.getEmail();
//        String otp = request.getOtp();
//
//        OtpData otpData = otpStorage.get(email);
//        if (otpData == null) {
//            throw new RuntimeException("OTP không tồn tại hoặc đã được sử dụng");
//        }
//
//        // kiểm tra hết hạn
////        if (System.currentTimeMillis() > otpData.getExpireAt()) {
////            otpStorage.remove(email);
////            pendingUsers.remove(email);
////            throw new RuntimeException("OTP đã hết hạn");
////        }
//        if (System.currentTimeMillis() > otpData.getExpireAt()) {
//            otpStorage.remove(email);
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "OTP đã hết hạn"
//            );
//        }
//
//
//        // kiểm tra OTP
//        if (!otpData.getOtp().equals(otp)) {
//            throw new RuntimeException("OTP không đúng");
//        }
//
//        // lấy dữ liệu đăng ký tạm
//        RegisterRequest registerRequest = pendingUsers.get(email);
//        if (registerRequest == null) {
//            throw new RuntimeException("Không tìm thấy dữ liệu đăng ký");
//        }
//        if (userRepository.existsByEmail(email)) {
//            throw new RuntimeException("Email đã được đăng ký");
//        }
//
//        // tạo user mới
//        User user = new User();
//        user.setEmail(registerRequest.getEmail());
//        user.setFullName(registerRequest.getFullName());
//        user.setRole(0); // USER
//        user.setPassword(
//                passwordEncoder.encode(registerRequest.getPassword())
//        );
//
//        userRepository.save(user);
//
//        // xoá dữ liệu tạm
//        otpStorage.remove(email);
//        pendingUsers.remove(email);
//
//        log.info("VERIFY OTP SUCCESS | email={}", email);
//    }
public void verifyOtp(VerifyOtpRequest request) {

    String email = request.getEmail();
    String otp = request.getOtp();

    OtpData otpData = otpStorage.get(email);
    if (otpData == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "OTP không tồn tại hoặc đã hết hạn"
        );
    }

    if (System.currentTimeMillis() > otpData.getExpireAt()) {
        otpStorage.remove(email);
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "OTP đã hết hạn"
        );
    }

    if (!otpData.getOtp().equals(otp)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "OTP không đúng"
        );
    }

    RegisterRequest registerRequest = pendingUsers.get(email);
    if (registerRequest == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Dữ liệu đăng ký không tồn tại"
        );
    }

    if (userRepository.existsByEmail(email)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email đã được đăng ký"
        );
    }

    User user = new User();
    user.setEmail(registerRequest.getEmail());
    user.setFullName(registerRequest.getFullName());
    user.setRole(0);
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

    userRepository.save(user);

    // ✅ CHỈ XOÁ KHI THÀNH CÔNG
    otpStorage.remove(email);
    pendingUsers.remove(email);

    log.info("VERIFY OTP SUCCESS | email={}", email);
}


    // ================= LOGIN =================
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        boolean match = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        log.info("LOGIN email={} match={}", request.getEmail(), match);

        if (!match) {
            throw new RuntimeException("Sai mật khẩu");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }


    // ================= SEND MAIL =================
    private void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject("Mã OTP xác thực");
            mail.setText("Mã OTP của bạn là: " + otp);
            mailSender.send(mail);
        } catch (Exception e) {
            log.error("SEND MAIL FAILED", e);
            throw new RuntimeException("SEND_MAIL_FAILED");
        }
    }



    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        long expireAt = System.currentTimeMillis() + 300_000; // 1 phút

        resetOtpStorage.put(request.getEmail(), new OtpData(otp, expireAt));
        sendOtpEmail(request.getEmail(), otp);

        log.info("FORGOT PASSWORD | email={} | otp={}", request.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {

        OtpData otpData = resetOtpStorage.get(request.getEmail());
        if (otpData == null) {
            throw new RuntimeException("OTP không tồn tại");
        }

        if (System.currentTimeMillis() > otpData.getExpireAt()) {
            resetOtpStorage.remove(request.getEmail());
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!otpData.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("OTP không đúng");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetOtpStorage.remove(request.getEmail());

        log.info("RESET PASSWORD OK | email={}", request.getEmail());
    }
//    public void resendOtp(String email) {
////        RegisterRequest data = pendingUsers.get(email);
////        if (data == null) {
////            throw new RuntimeException("REGISTER_DATA_NOT_FOUND");
////        }
//        if (!pendingUsers.containsKey(email)) {
//            throw new RuntimeException("Không thể gửi lại OTP, vui lòng đăng ký lại");
//        }
//
//        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
//        long expireAt = System.currentTimeMillis() + 60_000;
//
//        otpStorage.put(email, new OtpData(otp, expireAt));
//        sendOtpEmail(email, otp);
//
//        log.info("RESEND OTP | email={} | otp={}", email, otp);
//    }
public void resendOtp(String email) {
    if (!pendingUsers.containsKey(email)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Không thể gửi lại OTP, vui lòng đăng ký lại"
        );
    }

    String otp = String.valueOf(new Random().nextInt(900000) + 100000);
    long expireAt = System.currentTimeMillis() + 60_000;

    otpStorage.put(email, new OtpData(otp, expireAt));
    sendOtpEmail(email, otp);
    log.info("RESEND OTP CHECK pendingUsers={}", pendingUsers.keySet());

    log.info("RESEND OTP | email={} | otp={}", email, otp);
}


}
