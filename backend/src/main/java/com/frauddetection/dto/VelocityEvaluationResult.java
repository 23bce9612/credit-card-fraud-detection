package com.frauddetection.dto;

import java.util.List;

public class VelocityEvaluationResult {

    private int riskScore;
    private List<String> reasons;

    public VelocityEvaluationResult(
            int riskScore,
            List<String> reasons
    ) {
        this.riskScore = riskScore;
        this.reasons = reasons;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public List<String> getReasons() {
        return reasons;
    }
}