package com.frauddetection.repository;

import com.frauddetection.entity.Transaction;
import com.frauddetection.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserOrderByTransactionTimeDesc(
            User user
    );

    List<Transaction> findByMerchantContainingIgnoreCase(
            String merchant
    );

    List<Transaction> findByLocationContainingIgnoreCase(
            String location
    );

    // Module 10: Historical average
    @Query("""
            SELECT AVG(t.amount)
            FROM Transaction t
            WHERE t.user.id = :userId
            """)
    Double findAverageTransactionAmountByUserId(
            @Param("userId") Long userId
    );

    // Module 11: Transaction count in a time window
    long countByUserIdAndTransactionTimeAfter(
            Long userId,
            LocalDateTime time
    );

    // Module 11: Total spending in a time window
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
            AND t.transactionTime >= :time
            """)
    BigDecimal findTotalAmountByUserIdSince(
            @Param("userId") Long userId,
            @Param("time") LocalDateTime time
    );

    // Module 12: Security history checks
    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndLocation(
            Long userId,
            String location
    );

    boolean existsByUserIdAndDeviceId(
            Long userId,
            String deviceId
    );

    boolean existsByUserIdAndIpAddress(
            Long userId,
            String ipAddress
    );
    // =====================================
    // Module 14: Fraud Analytics
    // =====================================

    long count();

    long countByStatus(
            String status
    );

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            """)
    BigDecimal getTotalTransactionAmount();


    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.status = :status
            """)
    BigDecimal getTotalTransactionAmountByStatus(
            @Param("status") String status
    );
    // Module 15: Filter transactions by status
    List<Transaction> findByStatusIgnoreCase(
            String status
    );
}