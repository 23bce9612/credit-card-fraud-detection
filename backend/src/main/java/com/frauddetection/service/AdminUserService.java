package com.frauddetection.service;

import com.frauddetection.dto.UserResponse;
import com.frauddetection.entity.User;
import com.frauddetection.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }


    /*
     * Get all users
     */
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    /*
     * Convert User entity to UserResponse
     */
    private UserResponse convertToResponse(
            User user
    ) {

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }
}