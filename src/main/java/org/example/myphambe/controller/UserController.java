package org.example.myphambe.controller;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.ChangePasswordRequest;
import org.example.myphambe.dto.UpdateProfileRequest;
import org.example.myphambe.dto.UserProfileDTO;
import org.example.myphambe.service.ChangePasswordService;
import org.example.myphambe.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ChangePasswordService changePasswordService;
    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {

        UserDetails userDetails =
                (UserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        changePasswordService.changePassword(userDetails.getUsername(), request);

        return "Đổi mật khẩu thành công";
    }
    @PutMapping("/profile")
    public UserProfileDTO updateProfile(@RequestBody UpdateProfileRequest request) {

        UserDetails userDetails =
                (UserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();

        return userService.updateProfile(email, request);
    }

    @GetMapping("/profile")
    public UserProfileDTO getProfile() {

        UserDetails userDetails =
                (UserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();

        return userService.getProfile(email);
    }

//    @PostMapping("/change-password")
//    public String changePassword(
//            Principal principal,
//            @RequestBody ChangePasswordRequest request
//    ) {
//        changePasswordService.changePassword(principal.getName(), request);
//        return "Đổi mật khẩu thành công";
//    }
//
//    // ✅ Lấy thông tin profile
//    @GetMapping("/profile")
//    public UserProfileDTO getProfile(Principal principal) {
//        return userService.getProfile(principal.getName());
//    }
//
//    // ✅ Cập nhật profile
//    @PutMapping("/profile")
//    public UserProfileDTO updateProfile(
//            Principal principal,
//            @RequestBody UpdateProfileRequest request
//    ) {
//        return userService.updateProfile(principal.getName(), request);
//    }
}