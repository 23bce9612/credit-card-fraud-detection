package com.frauddetection.service;

import com.frauddetection.dto.VelocityEvaluationResult;
import com.frauddetection.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VelocityDetectionService {

    private final TransactionRepository transactionRepository;

    public VelocityDetectionService(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository =
                transactionRepository;
    }

    public VelocityEvaluationResult evaluateVelocity(
            Long userId
    ) {

        int riskScore = 0;

        List<String> reasons =
                new ArrayList<>();

        /*
         * Check transactions in last 5 minutes
         */
        LocalDateTime fiveMinutesAgo =
                LocalDateTime.now()
                        .minusMinutes(5);

        long recentTransactionCount =
                transactionRepository
                        .countByUserIdAndTransactionTimeAfter(
                                userId,
                                fiveMinutesAgo
                        );

        /*
         * Rule 1:
         * Very high transaction frequency
         */
        if (recentTransactionCount >= 10) {

            riskScore += 40;

            reasons.add(
                    "Very high transaction frequency detected in the last 5 minutes"
            );

        }

        /*
         * Rule 2:
         * High transaction frequency
         */
        else if (recentTransactionCount >= 5) {

            riskScore += 25;

            reasons.add(
                    "Multiple transactions detected in the last 5 minutes"
            );
        }

        /*
         * Calculate total spending in last 5 minutes
         */
        BigDecimal recentTotalAmount =
                transactionRepository
                        .findTotalAmountByUserIdSince(
                                userId,
                                fiveMinutesAgo
                        );

        /*
         * Rule 3:
         * Rapid high spending
         */
        if (recentTotalAmount != null
                && recentTotalAmount.compareTo(
                BigDecimal.valueOf(100000)
        ) > 0) {

            riskScore += 20;

            reasons.add(
                    "High total spending detected within a short time period"
            );
        }

        return new VelocityEvaluationResult(
                riskScore,
                reasons
        );
    }
}