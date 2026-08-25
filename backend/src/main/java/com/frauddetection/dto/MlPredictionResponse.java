package com.frauddetection.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MlPredictionResponse {

    private String prediction;

    private double fraudProbability;
}