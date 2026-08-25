package com.frauddetection.dto;

import java.util.List;

public class FraudDecisionResult {

    private int finalRiskScore;

    private String decision;

    private List<String> reasons;

    public FraudDecisionResult(
            int finalRiskScore,
            String decision,
            List<String> reasons
    ) {
        this.finalRiskScore = finalRiskScore;
        this.decision = decision;
        this.reasons = reasons;
    }

    public int getFinalRiskScore() {
        return finalRiskScore;
    }

    public String getDecision() {
        return decision;
    }

    public List<String> getReasons() {
        return reasons;
    }
}