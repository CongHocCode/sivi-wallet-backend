package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdAndIsDeletedFalse(Long walletId);
}