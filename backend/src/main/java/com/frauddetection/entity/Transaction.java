package com.frauddetection.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @Column(length = 100)
    private String merchant;

    @Column(length = 100)
    private String location;

    @Column(length = 30)
    private String cardType;

    @Column(length = 50)
    private String deviceType;

    @Column(length = 100)
    private String deviceId;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false, length = 20)
    private String status;
}