package com.frauddetection.service;

import com.frauddetection.dto.RuleEvaluationResult;
import com.frauddetection.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudRuleService {

    private final TransactionRepository transactionRepository;

    public FraudRuleService(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository =
                transactionRepository;
    }

    public RuleEvaluationResult evaluateRules(
            Long userId,
            BigDecimal amount
    ) {

        int riskScore = 0;

        List<String> reasons =
                new ArrayList<>();

        /*
         * Rule 1:
         * High-value transaction
         */
        if (amount.compareTo(
                BigDecimal.valueOf(50000)
        ) > 0) {

            riskScore += 20;

            reasons.add(
                    "High-value transaction detected"
            );
        }

        /*
         * Get user's historical average
         */
        Double averageAmount =
                transactionRepository
                        .findAverageTransactionAmountByUserId(
                                userId
                        );

        /*
         * Rule 2:
         * Compare with user's historical average
         */
        if (averageAmount != null
                && averageAmount > 0) {

            BigDecimal average =
                    BigDecimal.valueOf(
                            averageAmount
                    );

            BigDecimal threshold =
                    average.multiply(
                            BigDecimal.valueOf(5)
                    );

            if (amount.compareTo(
                    threshold
            ) > 0) {

                riskScore += 30;

                reasons.add(
                        "Transaction amount is significantly higher than the user's average spending"
                );
            }
        }

        return new RuleEvaluationResult(
                riskScore,
                reasons
        );
    }
}