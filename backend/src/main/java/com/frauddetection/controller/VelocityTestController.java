package com.frauddetection.controller;

import com.frauddetection.dto.VelocityEvaluationResult;
import com.frauddetection.service.VelocityDetectionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/velocity")
public class VelocityTestController {

    private final VelocityDetectionService velocityDetectionService;

    public VelocityTestController(
            VelocityDetectionService velocityDetectionService
    ) {
        this.velocityDetectionService = velocityDetectionService;
    }

    @GetMapping("/evaluate")
    public ResponseEntity<VelocityEvaluationResult> evaluate(
            @RequestParam Long userId
    ) {

        VelocityEvaluationResult result =
                velocityDetectionService.evaluateVelocity(userId);

        return ResponseEntity.ok(result);
    }
}