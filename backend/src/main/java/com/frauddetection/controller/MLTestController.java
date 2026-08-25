package com.frauddetection.controller;

import com.frauddetection.service.MLService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.frauddetection.dto.MlPredictionResponse;
@RestController
@RequestMapping("/api/ml")
public class MLTestController {

    private final MLService mlService;

    public MLTestController(MLService mlService) {
        this.mlService = mlService;
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predict(
            @RequestBody Map<String, Object> transactionData
    ) {

        MlPredictionResponse prediction =
                mlService.getPrediction(transactionData);

        return ResponseEntity.ok(
                prediction
        );
    }
}