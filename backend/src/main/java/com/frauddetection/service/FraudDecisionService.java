package com.frauddetection.service;

import com.frauddetection.dto.FraudDecisionResult;
import com.frauddetection.dto.MlPredictionResponse;
import com.frauddetection.dto.RuleEvaluationResult;
import com.frauddetection.dto.SecurityEvaluationResult;
import com.frauddetection.dto.VelocityEvaluationResult;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDecisionService {

    public FraudDecisionResult makeDecision(

            MlPredictionResponse mlResult,

            RuleEvaluationResult ruleResult,

            VelocityEvaluationResult velocityResult,

            SecurityEvaluationResult securityResult
    ) {

        /*
         * 1. ML Risk
         * Maximum = 50
         */
        double mlRisk =
                mlResult.getFraudProbability() * 50;


        /*
         * 2. Rule Risk
         * Maximum = 20
         */
        int ruleRisk =
                Math.min(
                        ruleResult.getRiskScore(),
                        20
                );


        /*
         * 3. Velocity Risk
         * Maximum = 15
         */
        int velocityRisk =
                Math.min(
                        velocityResult.getRiskScore(),
                        15
                );


        /*
         * 4. Security Risk
         * Maximum = 15
         */
        int securityRisk =
                Math.min(
                        securityResult.getRiskScore(),
                        15
                );


        /*
         * Calculate final risk score
         */
        int finalRiskScore =
                (int) Math.round(
                        mlRisk
                                + ruleRisk
                                + velocityRisk
                                + securityRisk
                );


        /*
         * Safety limit
         */
        finalRiskScore =
                Math.min(finalRiskScore, 100);


        /*
         * Combine all reasons
         */
        List<String> reasons =
                new ArrayList<>();

        reasons.addAll(
                ruleResult.getReasons()
        );

        reasons.addAll(
                velocityResult.getReasons()
        );

        reasons.addAll(
                securityResult.getReasons()
        );


        /*
         * Add ML reason
         */
        if (mlResult.getFraudProbability() >= 0.70) {

            reasons.add(
                    "Machine learning model detected a high fraud probability"
            );

        } else if (mlResult.getFraudProbability() >= 0.40) {

            reasons.add(
                    "Machine learning model detected a moderate fraud probability"
            );
        }


        /*
         * Strong fraud signal detection
         *
         * Example:
         *
         * High-value transaction
         * + New location/device/IP
         *
         * These multiple independent signals can
         * trigger an immediate block even if the
         * ML model probability is not very high.
         */
        boolean highValueTransaction =
                ruleResult.getRiskScore() >= 20;

        boolean highSecurityRisk =
                securityResult.getRiskScore() >= 30;


        /*
         * Final decision
         */
        String decision;

        if (finalRiskScore >= 70) {

            decision = "BLOCKED";

        } else if (
                highValueTransaction
                        && highSecurityRisk
        ) {

            decision = "BLOCKED";

            reasons.add(
                    "Transaction blocked due to multiple strong fraud indicators"
            );

        } else if (finalRiskScore >= 40) {

            decision = "REVIEW";

        } else {

            decision = "APPROVED";
        }


        return new FraudDecisionResult(
                finalRiskScore,
                decision,
                reasons
        );
    }
}