package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserIdAndIsActiveTrue(Long userId);
    Optional<Wallet> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
    Long countByUserIdAndIsActiveTrue(Long userId);
}