package com.frauddetection.controller;

import com.frauddetection.dto.RegisterRequest;
import com.frauddetection.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {

        userService.register(request);

        return "User Registered Successfully";
    }
}