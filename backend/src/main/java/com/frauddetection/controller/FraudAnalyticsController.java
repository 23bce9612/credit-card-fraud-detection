package com.frauddetection.controller;

import com.frauddetection.dto.FraudAnalyticsResponse;
import com.frauddetection.service.FraudAnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
public class FraudAnalyticsController {

    private final FraudAnalyticsService fraudAnalyticsService;

    public FraudAnalyticsController(
            FraudAnalyticsService fraudAnalyticsService
    ) {
        this.fraudAnalyticsService =
                fraudAnalyticsService;
    }


    /*
     * Get complete fraud analytics
     */
    @GetMapping
    public ResponseEntity<FraudAnalyticsResponse>
    getAnalytics() {

        FraudAnalyticsResponse analytics =
                fraudAnalyticsService.getAnalytics();

        return ResponseEntity.ok(
                analytics
        );
    }
}