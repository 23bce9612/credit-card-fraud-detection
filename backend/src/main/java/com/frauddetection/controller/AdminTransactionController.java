package com.frauddetection.controller;

import com.frauddetection.dto.TransactionResponse;
import com.frauddetection.dto.TransactionStatusUpdateRequest;
import com.frauddetection.service.TransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;


    /*
     * Get all transactions
     */
    @GetMapping
    public List<TransactionResponse> getAllTransactions() {

        return transactionService.getAllTransactions();
    }

    /*
     * Get all transactions requiring admin review
     *
     * GET /api/admin/transactions/review
     */
    @GetMapping("/review")
    public List<TransactionResponse> getReviewTransactions() {

        return transactionService.getReviewTransactions();
    }

    /*
     * Search by merchant
     *
     * GET /api/admin/transactions/search?merchant=Amazon
     */
    @GetMapping("/search")
    public List<TransactionResponse> searchTransactions(
            @RequestParam String merchant
    ) {

        return transactionService.searchByMerchant(
                merchant
        );
    }


    /*
     * Filter by location
     *
     * GET /api/admin/transactions/location?location=Hyderabad
     */
    @GetMapping("/location")
    public List<TransactionResponse> searchByLocation(
            @RequestParam String location
    ) {

        return transactionService.searchByLocation(
                location
        );
    }


    /*
     * Filter by status
     *
     * GET /api/admin/transactions/status?status=REVIEW
     */
    @GetMapping("/status")
    public List<TransactionResponse> filterByStatus(
            @RequestParam String status
    ) {

        return transactionService.filterByStatus(
                status
        );
    }


    /*
     * Update transaction status
     *
     * PUT /api/admin/transactions/{transactionId}/status
     */
    @PutMapping("/{transactionId}/status")
    public TransactionResponse updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestBody TransactionStatusUpdateRequest request
    ) {

        return transactionService.updateTransactionStatus(
                transactionId,
                request.getStatus()
        );
    }
}