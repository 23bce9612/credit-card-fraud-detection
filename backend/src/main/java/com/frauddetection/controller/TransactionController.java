package com.frauddetection.controller;

import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.dto.TransactionResponse;
import com.frauddetection.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionResponse createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        return transactionService.createTransaction(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/my")
    public List<TransactionResponse> getMyTransactions(
            Authentication authentication) {

        return transactionService.getMyTransactions(
                authentication.getName()
        );
    }
}