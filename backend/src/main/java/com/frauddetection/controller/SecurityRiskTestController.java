package com.frauddetection.controller;

import com.frauddetection.dto.SecurityEvaluationResult;
import com.frauddetection.service.SecurityRiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityRiskTestController {

    private final SecurityRiskService securityRiskService;

    @GetMapping("/evaluate")
    public ResponseEntity<SecurityEvaluationResult> evaluate(

            @RequestParam Long userId,

            @RequestParam String location,

            @RequestParam String deviceId,

            @RequestParam String ipAddress
    ) {

        SecurityEvaluationResult result =
                securityRiskService.evaluateSecurityRisk(
                        userId,
                        location,
                        deviceId,
                        ipAddress
                );

        return ResponseEntity.ok(result);
    }
}