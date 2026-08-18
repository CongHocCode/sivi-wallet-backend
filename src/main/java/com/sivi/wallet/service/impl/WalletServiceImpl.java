package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.wallet.WalletListResponse;
import com.sivi.wallet.dto.wallet.WalletRequest;
import com.sivi.wallet.dto.wallet.WalletResponse;
import com.sivi.wallet.dto.wallet.WalletTransferRequest;
import com.sivi.wallet.entity.Wallet;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.mapper.WalletMapper;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.repository.WalletRepository;
import com.sivi.wallet.service.WalletService;
import com.sivi.wallet.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.sivi.wallet.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional(readOnly = true)
    public WalletListResponse getAllWallets() {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        List<Wallet> wallets = walletRepository.findByUserIdAndIsActiveTrue(currentUserId);
        BigDecimal sum = wallets.stream().map(Wallet::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        return WalletListResponse.builder().wallets(wallets.stream().map(WalletMapper::toResponse).toList()).totalBalance(sum).build();
    }

    @Override
    @Transactional
    public WalletResponse createWallet(WalletRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        Wallet wallet = WalletMapper.toEntity(request, currentUserId);
        Wallet savedWallet = walletRepository.save(wallet);
        return WalletMapper.toResponse(savedWallet);
    }

    @Override
    @Transactional
    public Void internalTransfer(WalletTransferRequest request) {
        // Block self transfer
        if (request.getFromWalletId().equals(request.getToWalletId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);

        Wallet fromWallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getFromWalletId(), currentUserId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Wallet toWallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getToWalletId(), currentUserId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // Check balance
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE); // Số dư không đủ
        }

        // Update balance
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));

        // @Transactional, Hibernate automatically save the changes
        return null;
    }

    @Override
    public Void deleteWallet(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(id, currentUserId)
                .orElseThrow(() -> new AppException(WALLET_NOT_FOUND));

        if (walletRepository.countByUserIdAndIsActiveTrue(currentUserId) <= 1) {
            throw new AppException(BAD_REQUEST); //Need at least 1 wallet
        }
        wallet.setActive(false);
        walletRepository.save(wallet);
        return null;
    }
}
