package org.example.myphambe.service;

import org.example.myphambe.dto.UserDTO;
import org.example.myphambe.entity.User;
import org.example.myphambe.repository.UserRepository;
import org.example.myphambe.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserAdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers(String search, Integer role) {
        List<User> users;
        if (search != null && !search.isEmpty()) {
            users = userRepository.searchUsers(search);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }

        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword("123456");
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());
        
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null) ? convertToDTO(user) : null;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);

        Long totalOrders = userRepository.countTotalOrdersByUserId(user.getId());
        Double totalSpent = userRepository.sumTotalSpentByUserId(user.getId());

        dto.setTotalOrders(totalOrders != null ? totalOrders : 0L);
        dto.setTotalSpent(totalSpent != null ? totalSpent : 0.0);

        String nameForAvatar = (user.getFullName() != null) ? user.getFullName() : "User";
        dto.setAvatar("https://ui-avatars.com/api/?name=" +
                nameForAvatar.replace(" ", "+") +
                "&background=b124b1&color=fff");

        return dto;
    }
}