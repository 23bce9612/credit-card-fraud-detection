package com.frauddetection.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotBlank
    private String merchant;

    @NotBlank
    private String location;

    @NotBlank
    private String cardType;

    @NotBlank
    private String deviceType;

    @NotBlank
    private String deviceId;

    @NotBlank
    private String ipAddress;
}