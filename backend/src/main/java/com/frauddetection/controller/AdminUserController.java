package com.frauddetection.controller;

import com.frauddetection.dto.UserResponse;
import com.frauddetection.service.AdminUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;


    /*
     * Get all users
     *
     * ADMIN access only because of:
     * /api/admin/**
     *     → hasRole("ADMIN")
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>>
    getAllUsers() {

        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }
}