package com.frauddetection.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String merchant;
    private String location;
    private String cardType;
    private String deviceType;
    private LocalDateTime transactionTime;
    private String status;
}