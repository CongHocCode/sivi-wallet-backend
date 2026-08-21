package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdAndIsDeletedFalse(Long walletId);

    @Query(value =
            "SELECT t.* FROM transactions t " +
            "JOIN wallets w ON t.wallet_id = w.id " +
            "WHERE w.user_id = :userId AND t.is_deleted = false " +
            "AND (:walletId IS NULL OR t.wallet_id = :walletId) " +
            "AND (:month IS NULL OR MONTH(t.transaction_date) = :month) " +
            "AND (:year IS NULL OR YEAR(t.transaction_date) = :year) " +
            "ORDER BY t.transaction_date DESC", nativeQuery = true)
    List<Transaction> filterTransactions(
            @Param("userId") Long userId,
            @Param("walletId") Long walletId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}