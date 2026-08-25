package com.frauddetection.controller;

import com.frauddetection.dto.FraudDecisionResult;
import com.frauddetection.dto.MlPredictionResponse;
import com.frauddetection.dto.RuleEvaluationResult;
import com.frauddetection.dto.SecurityEvaluationResult;
import com.frauddetection.dto.VelocityEvaluationResult;

import com.frauddetection.service.FraudDecisionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-decision")
public class FraudDecisionTestController {

    private final FraudDecisionService fraudDecisionService;

    public FraudDecisionTestController(
            FraudDecisionService fraudDecisionService
    ) {
        this.fraudDecisionService =
                fraudDecisionService;
    }


    @GetMapping("/test")
    public ResponseEntity<FraudDecisionResult> testDecision() {

        /*
         * Temporary ML test data
         */
        MlPredictionResponse mlPrediction =
                new MlPredictionResponse();

        mlPrediction.setPrediction("FRAUD");

        mlPrediction.setFraudProbability(0.90);


        /*
         * Temporary Rule Engine test data
         */
        RuleEvaluationResult ruleResult =
                new RuleEvaluationResult(
                        20,
                        List.of(
                                "High-value transaction detected"
                        )
                );


        /*
         * Temporary Velocity Engine test data
         */
        VelocityEvaluationResult velocityResult =
                new VelocityEvaluationResult(
                        25,
                        List.of(
                                "Multiple transactions detected in the last 5 minutes"
                        )
                );


        /*
         * Temporary Security Engine test data
         */
        SecurityEvaluationResult securityResult =
                new SecurityEvaluationResult(
                        15,
                        List.of(
                                "New location detected"
                        )
                );


        /*
         * Final Fraud Decision
         */
        FraudDecisionResult result =
                fraudDecisionService.makeDecision(
                        mlPrediction,
                        ruleResult,
                        velocityResult,
                        securityResult
                );


        return ResponseEntity.ok(result);
    }
}