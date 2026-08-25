package com.frauddetection.service.impl;

import com.frauddetection.dto.FraudDecisionResult;
import com.frauddetection.dto.MlPredictionResponse;
import com.frauddetection.dto.RuleEvaluationResult;
import com.frauddetection.dto.SecurityEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.dto.TransactionResponse;
import com.frauddetection.dto.VelocityEvaluationResult;

import com.frauddetection.entity.Transaction;
import com.frauddetection.entity.User;

import com.frauddetection.repository.TransactionRepository;
import com.frauddetection.repository.UserRepository;

import com.frauddetection.service.FraudDecisionService;
import com.frauddetection.service.FraudRuleService;
import com.frauddetection.service.MLService;
import com.frauddetection.service.SecurityRiskService;
import com.frauddetection.service.TransactionService;
import com.frauddetection.service.VelocityDetectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl
        implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private final FraudRuleService fraudRuleService;

    private final VelocityDetectionService velocityDetectionService;

    private final SecurityRiskService securityRiskService;

    private final FraudDecisionService fraudDecisionService;

    private final MLService mlService;


    @Override
    public TransactionResponse createTransaction(
            TransactionRequest request,
            String email) {

        /*
         * Step 1:
         * Get logged-in user
         */
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        /*
         * Step 2:
         * Rule Engine
         */
        RuleEvaluationResult ruleResult =
                fraudRuleService.evaluateRules(
                        user.getId(),
                        request.getAmount()
                );


        /*
         * Step 3:
         * Velocity Engine
         */
        VelocityEvaluationResult velocityResult =
                velocityDetectionService.evaluateVelocity(
                        user.getId()
                );


        /*
         * Step 4:
         * Security Engine
         */
        SecurityEvaluationResult securityResult =
                securityRiskService.evaluateSecurityRisk(
                        user.getId(),
                        request.getLocation(),
                        request.getDeviceId(),
                        request.getIpAddress()
                );


        /*
         * Step 5:
         * Calculate ML features
         */

        // Historical average amount
        Double averageAmount =
                transactionRepository
                        .findAverageTransactionAmountByUserId(
                                user.getId()
                        );

        double historicalAverageAmount =
                averageAmount != null
                        ? averageAmount
                        : request.getAmount().doubleValue();


        // Transactions in the last 5 minutes
        LocalDateTime fiveMinutesAgo =
                LocalDateTime.now()
                        .minusMinutes(5);

        long recentTransactionCount =
                transactionRepository
                        .countByUserIdAndTransactionTimeAfter(
                                user.getId(),
                                fiveMinutesAgo
                        );


        // Total spending in the last 5 minutes
        BigDecimal recentTotalAmount =
                transactionRepository
                        .findTotalAmountByUserIdSince(
                                user.getId(),
                                fiveMinutesAgo
                        );


        /*
         * Security features for ML
         */

        boolean knownLocation =
                transactionRepository
                        .existsByUserIdAndLocation(
                                user.getId(),
                                request.getLocation()
                        );

        boolean knownDevice =
                transactionRepository
                        .existsByUserIdAndDeviceId(
                                user.getId(),
                                request.getDeviceId()
                        );

        boolean knownIpAddress =
                transactionRepository
                        .existsByUserIdAndIpAddress(
                                user.getId(),
                                request.getIpAddress()
                        );


        /*
         * Convert boolean values into ML features
         *
         * New = 1
         * Known = 0
         */

        int isNewLocation =
                knownLocation ? 0 : 1;

        int isNewDevice =
                knownDevice ? 0 : 1;

        int isNewIpAddress =
                knownIpAddress ? 0 : 1;


        /*
         * Step 6:
         * Build ML request
         */

        Map<String, Object> mlData =
                new HashMap<>();

        mlData.put(
                "amount",
                request.getAmount().doubleValue()
        );

        mlData.put(
                "historicalAverageAmount",
                historicalAverageAmount
        );

        mlData.put(
                "recentTransactionCount",
                recentTransactionCount
        );

        mlData.put(
                "recentTotalAmount",
                recentTotalAmount.doubleValue()
        );

        mlData.put(
                "isNewLocation",
                isNewLocation
        );

        mlData.put(
                "isNewDevice",
                isNewDevice
        );

        mlData.put(
                "isNewIpAddress",
                isNewIpAddress
        );


        /*
         * Step 7:
         * Real ML prediction
         */

        MlPredictionResponse mlResult =
                mlService.getPrediction(
                        mlData
                );


        /*
         * Step 8:
         * Final Fraud Decision
         */

        FraudDecisionResult fraudDecision =
                fraudDecisionService.makeDecision(
                        mlResult,
                        ruleResult,
                        velocityResult,
                        securityResult
                );


        /*
         * Step 9:
         * Create transaction
         */

        Transaction transaction = new Transaction();

        transaction.setUser(user);

        transaction.setAmount(
                request.getAmount()
        );

        transaction.setMerchant(
                request.getMerchant()
        );

        transaction.setLocation(
                request.getLocation()
        );

        transaction.setCardType(
                request.getCardType()
        );

        transaction.setDeviceType(
                request.getDeviceType()
        );

        transaction.setDeviceId(
                request.getDeviceId()
        );

        transaction.setIpAddress(
                request.getIpAddress()
        );

        transaction.setTransactionTime(
                LocalDateTime.now()
        );


        /*
         * Step 10:
         * Save final fraud decision
         */

        transaction.setStatus(
                fraudDecision.getDecision()
        );


        /*
         * Step 11:
         * Save transaction
         */

        Transaction saved =
                transactionRepository.save(
                        transaction
                );


        /*
         * Step 12:
         * Return response
         */

        return convertToResponse(saved);
    }


    @Override
    public List<TransactionResponse> getMyTransactions(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return transactionRepository
                .findByUserOrderByTransactionTimeDesc(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    @Override
    public List<TransactionResponse> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getReviewTransactions() {

        return transactionRepository
                .findByStatusIgnoreCase("REVIEW")
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> searchByMerchant(
            String merchant
    ) {

        return transactionRepository
                .findByMerchantContainingIgnoreCase(
                        merchant
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> searchByLocation(
            String location
    ) {

        return transactionRepository
                .findByLocationContainingIgnoreCase(
                        location
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    @Override
    public List<TransactionResponse> filterByStatus(
            String status
    ) {

        return transactionRepository
                .findByStatusIgnoreCase(
                        status
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    /*
     * Module 15:
     * Admin manually updates transaction status
     */
    @Override
    public TransactionResponse updateTransactionStatus(
            Long transactionId,
            String status
    ) {

        /*
         * Find transaction
         */
        Transaction transaction =
                transactionRepository
                        .findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"
                                )
                        );


        /*
         * Normalize status
         */
        String normalizedStatus =
                status.toUpperCase();


        /*
         * Validate status
         *
         * Only APPROVED or BLOCKED
         * can be manually selected by admin.
         */
        if (!normalizedStatus.equals("APPROVED")
                && !normalizedStatus.equals("BLOCKED")) {

            throw new RuntimeException(
                    "Invalid status. Only APPROVED or BLOCKED are allowed"
            );
        }


        /*
         * Update transaction status
         */
        transaction.setStatus(
                normalizedStatus
        );


        /*
         * Save updated transaction
         */
        Transaction updatedTransaction =
                transactionRepository.save(
                        transaction
                );


        /*
         * Return updated transaction
         */
        return convertToResponse(
                updatedTransaction
        );
    }


    private TransactionResponse convertToResponse(
            Transaction transaction
    ) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getMerchant(),
                transaction.getLocation(),
                transaction.getCardType(),
                transaction.getDeviceType(),
                transaction.getTransactionTime(),
                transaction.getStatus()
        );
    }
}