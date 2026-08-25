package com.frauddetection.service.impl;

import com.frauddetection.dto.RegisterRequest;
import com.frauddetection.entity.Role;
import com.frauddetection.entity.User;
import com.frauddetection.repository.RoleRepository;
import com.frauddetection.repository.UserRepository;
import com.frauddetection.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder encoder;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow();

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        user.setPassword(encoder.encode(request.getPassword()));

        user.setCreatedAt(LocalDateTime.now());

        user.setRole(role);

        userRepository.save(user);
    }
}