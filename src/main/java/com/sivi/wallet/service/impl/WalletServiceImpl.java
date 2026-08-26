package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.wallet.WalletListResponse;
import com.sivi.wallet.dto.wallet.WalletRequest;
import com.sivi.wallet.dto.wallet.WalletResponse;
import com.sivi.wallet.dto.wallet.WalletTransferRequest;
import com.sivi.wallet.entity.Transaction;
import com.sivi.wallet.entity.Wallet;
import com.sivi.wallet.enums.SourceType;
import com.sivi.wallet.enums.TransactionType;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.mapper.WalletMapper;
import com.sivi.wallet.repository.TransactionRepository;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.repository.WalletRepository;
import com.sivi.wallet.service.WalletService;
import com.sivi.wallet.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.sivi.wallet.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

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

        Wallet fromWallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getFromWalletId(), currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Wallet toWallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getToWalletId(), currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // Check balance
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE); // Số dư không đủ
        }

        // Update balance
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));

        // Record transfer transactions for both wallets
        String transferNote = (request.getNote() != null && !request.getNote().isBlank())
                ? request.getNote() : "Chuyển tiền nội bộ";

        // Outgoing transfer transaction
        Transaction outTx = Transaction.builder()
                .walletId(fromWallet.getId())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .note("Chuyển sang " + toWallet.getName() + ": " + transferNote)
                .sourceType(SourceType.MANUAL)
                .isDeleted(false)
                .transactionDate(LocalDateTime.now())
                .build();
        transactionRepository.save(outTx);

        // Incoming transfer transaction
        Transaction inTx = Transaction.builder()
                .walletId(toWallet.getId())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .note("Nhận từ " + fromWallet.getName() + ": " + transferNote)
                .sourceType(SourceType.MANUAL)
                .isDeleted(false)
                .transactionDate(LocalDateTime.now())
                .build();
        transactionRepository.save(inTx);

        // @Transactional, Hibernate automatically save the changes
        return null;
    }

    @Override
    @Transactional
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