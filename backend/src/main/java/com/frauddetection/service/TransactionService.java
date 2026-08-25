package com.frauddetection.service;

import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(
            TransactionRequest request,
            String email
    );

    List<TransactionResponse> getMyTransactions(
            String email
    );

    List<TransactionResponse> getAllTransactions();

    List<TransactionResponse> getReviewTransactions();

    List<TransactionResponse> searchByMerchant(
            String merchant
    );

    List<TransactionResponse> searchByLocation(
            String location
    );

    List<TransactionResponse> filterByStatus(
            String status
    );

    TransactionResponse updateTransactionStatus(
            Long transactionId,
            String status
    );
}