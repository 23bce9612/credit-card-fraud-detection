package com.frauddetection.controller;

import com.frauddetection.dto.RuleEvaluationResult;
import com.frauddetection.service.FraudRuleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/rules")
public class FraudRuleTestController {

    private final FraudRuleService fraudRuleService;

    public FraudRuleTestController(
            FraudRuleService fraudRuleService
    ) {
        this.fraudRuleService = fraudRuleService;
    }

    @GetMapping("/evaluate")
    public ResponseEntity<RuleEvaluationResult> evaluate(

            @RequestParam Long userId,

            @RequestParam BigDecimal amount
    ) {

        RuleEvaluationResult result =
                fraudRuleService.evaluateRules(
                        userId,
                        amount
                );

        return ResponseEntity.ok(result);
    }
}