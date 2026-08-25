package com.frauddetection.service;

import com.frauddetection.dto.MlPredictionResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MLService {

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;

    public MLService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MlPredictionResponse getPrediction(
            Map<String, Object> transactionData
    ) {

        try {

            String url = mlServiceUrl + "/predict";

            ResponseEntity<MlPredictionResponse> response =
                    restTemplate.postForEntity(
                            url,
                            transactionData,
                            MlPredictionResponse.class
                    );

            return response.getBody();

        } catch (RestClientException exception) {

            throw new RuntimeException(
                    "ML prediction service is unavailable"
            );
        }
    }
}