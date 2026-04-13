package org.example.myphambe.service;

import lombok.RequiredArgsConstructor;
import org.example.myphambe.dto.UpdateProfileRequest;
import org.example.myphambe.dto.UserProfileDTO;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    public UserProfileDTO updateProfile(String email, UpdateProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(req.getFullName());
        user.setAddress(req.getAddress());
        user.setPhone(req.getPhone());

        userRepository.save(user);

        return mapToDTO(user);
    }

    private UserProfileDTO mapToDTO(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone()
        );
    }
}