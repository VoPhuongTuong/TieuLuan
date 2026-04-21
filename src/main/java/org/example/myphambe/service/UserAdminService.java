package org.example.myphambe.service;

import org.example.myphambe.dto.UserDTO;

import java.util.List;

public interface UserAdminService {
    List<UserDTO> getAllUsers(String search, Integer role);
    UserDTO getUserById(Integer id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Integer id, UserDTO userDTO);
    void deleteUser(Integer id);
}
