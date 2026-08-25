package com.frauddetection.service;

import com.frauddetection.dto.FraudAnalyticsResponse;
import com.frauddetection.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FraudAnalyticsService {

    private final TransactionRepository transactionRepository;

    public FraudAnalyticsService(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository =
                transactionRepository;
    }


    public FraudAnalyticsResponse getAnalytics() {

        /*
         * Transaction counts
         */
        long totalTransactions =
                transactionRepository.count();

        long approvedTransactions =
                transactionRepository.countByStatus(
                        "APPROVED"
                );

        long reviewTransactions =
                transactionRepository.countByStatus(
                        "REVIEW"
                );

        long blockedTransactions =
                transactionRepository.countByStatus(
                        "BLOCKED"
                );


        /*
         * Transaction amounts
         */
        BigDecimal totalTransactionAmount =
                transactionRepository
                        .getTotalTransactionAmount();

        BigDecimal approvedTransactionAmount =
                transactionRepository
                        .getTotalTransactionAmountByStatus(
                                "APPROVED"
                        );

        BigDecimal reviewTransactionAmount =
                transactionRepository
                        .getTotalTransactionAmountByStatus(
                                "REVIEW"
                        );

        BigDecimal blockedTransactionAmount =
                transactionRepository
                        .getTotalTransactionAmountByStatus(
                                "BLOCKED"
                        );


        /*
         * Calculate fraud rate
         *
         * Fraud Rate =
         * Blocked Transactions / Total Transactions × 100
         */
        double fraudRate = 0.0;

        if (totalTransactions > 0) {

            fraudRate =
                    ((double) blockedTransactions
                            / totalTransactions)
                            * 100;
        }


        /*
         * Return analytics response
         */
        return new FraudAnalyticsResponse(
                totalTransactions,
                approvedTransactions,
                reviewTransactions,
                blockedTransactions,
                totalTransactionAmount,
                approvedTransactionAmount,
                reviewTransactionAmount,
                blockedTransactionAmount,
                fraudRate
        );
    }
}