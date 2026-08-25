package com.frauddetection.dto;

import java.math.BigDecimal;

public class FraudAnalyticsResponse {

    private long totalTransactions;

    private long approvedTransactions;

    private long reviewTransactions;

    private long blockedTransactions;

    private BigDecimal totalTransactionAmount;

    private BigDecimal approvedTransactionAmount;

    private BigDecimal reviewTransactionAmount;

    private BigDecimal blockedTransactionAmount;

    private double fraudRate;


    public FraudAnalyticsResponse(
            long totalTransactions,
            long approvedTransactions,
            long reviewTransactions,
            long blockedTransactions,
            BigDecimal totalTransactionAmount,
            BigDecimal approvedTransactionAmount,
            BigDecimal reviewTransactionAmount,
            BigDecimal blockedTransactionAmount,
            double fraudRate
    ) {
        this.totalTransactions = totalTransactions;
        this.approvedTransactions = approvedTransactions;
        this.reviewTransactions = reviewTransactions;
        this.blockedTransactions = blockedTransactions;
        this.totalTransactionAmount = totalTransactionAmount;
        this.approvedTransactionAmount = approvedTransactionAmount;
        this.reviewTransactionAmount = reviewTransactionAmount;
        this.blockedTransactionAmount = blockedTransactionAmount;
        this.fraudRate = fraudRate;
    }


    public long getTotalTransactions() {
        return totalTransactions;
    }

    public long getApprovedTransactions() {
        return approvedTransactions;
    }

    public long getReviewTransactions() {
        return reviewTransactions;
    }

    public long getBlockedTransactions() {
        return blockedTransactions;
    }

    public BigDecimal getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public BigDecimal getApprovedTransactionAmount() {
        return approvedTransactionAmount;
    }

    public BigDecimal getReviewTransactionAmount() {
        return reviewTransactionAmount;
    }

    public BigDecimal getBlockedTransactionAmount() {
        return blockedTransactionAmount;
    }

    public double getFraudRate() {
        return fraudRate;
    }
}