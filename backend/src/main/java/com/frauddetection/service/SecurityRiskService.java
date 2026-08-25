package com.frauddetection.service;

import com.frauddetection.dto.SecurityEvaluationResult;
import com.frauddetection.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityRiskService {

    private final TransactionRepository transactionRepository;

    public SecurityRiskService(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository = transactionRepository;
    }

    public SecurityEvaluationResult evaluateSecurityRisk(
            Long userId,
            String location,
            String deviceId,
            String ipAddress
    ) {

        int riskScore = 0;

        List<String> reasons = new ArrayList<>();

        /*
         * Check whether the user has any
         * previous transaction history.
         */
        boolean hasTransactionHistory =
                transactionRepository.existsByUserId(userId);

        /*
         * First transaction:
         * Do not mark everything as suspicious.
         */
        if (!hasTransactionHistory) {

            return new SecurityEvaluationResult(
                    0,
                    reasons
            );
        }

        /*
         * Check location history
         */
        boolean knownLocation =
                transactionRepository
                        .existsByUserIdAndLocation(
                                userId,
                                location
                        );

        if (!knownLocation) {

            riskScore += 15;

            reasons.add(
                    "New location detected"
            );
        }

        /*
         * Check device history
         */
        boolean knownDevice =
                transactionRepository
                        .existsByUserIdAndDeviceId(
                                userId,
                                deviceId
                        );

        if (!knownDevice) {

            riskScore += 20;

            reasons.add(
                    "New device detected"
            );
        }

        /*
         * Check IP address history
         */
        boolean knownIpAddress =
                transactionRepository
                        .existsByUserIdAndIpAddress(
                                userId,
                                ipAddress
                        );

        if (!knownIpAddress) {

            riskScore += 10;

            reasons.add(
                    "New IP address detected"
            );
        }

        return new SecurityEvaluationResult(
                riskScore,
                reasons
        );
    }
}