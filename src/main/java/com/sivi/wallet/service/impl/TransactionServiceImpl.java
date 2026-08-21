package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.transaction.TransactionRequest;
import com.sivi.wallet.dto.transaction.TransactionResponse;
import com.sivi.wallet.entity.Category;
import com.sivi.wallet.entity.Transaction;
import com.sivi.wallet.entity.Wallet;
import com.sivi.wallet.enums.CategoryType;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.mapper.TransactionMapper;
import com.sivi.wallet.repository.CategoryRepository;
import com.sivi.wallet.repository.TransactionRepository;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.repository.WalletRepository;
import com.sivi.wallet.service.TransactionService;
import com.sivi.wallet.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository txRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        Transaction tx = TransactionMapper.toEntity(request);

        // Find wallet and category to ensure authority
        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(tx.getWalletId(), currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Category category = categoryRepository.findAccessibleCategory(tx.getCategoryId(), currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // 2. Kiểm tra khớp Loại giao dịch vs Loại danh mục
        if (!request.getType().name().equals(category.getType().name())) {
            throw new AppException(ErrorCode.BAD_REQUEST); // "Loại giao dịch không khớp với danh mục"
        }

        // Calculate balance
        if (category.getType() == CategoryType.EXPENSE) {
            if (wallet.getBalance().compareTo(tx.getAmount()) < 0) {
                throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
            }
            wallet.setBalance(wallet.getBalance().subtract(tx.getAmount()));
        } else if (category.getType() == CategoryType.INCOME) {
            wallet.setBalance(wallet.getBalance().add(tx.getAmount()));
        }

        // Save data and response
        Transaction savedTx = txRepository.save(tx);
        return TransactionMapper.toResponse(savedTx, wallet, category);
    }

    @Override
    public List<TransactionResponse> getTransactions(Integer month, Integer year, Long walletId) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        List<Transaction> txs = txRepository.filterTransactions(currentUserId, walletId, month, year);

        Map<Long, Wallet> walletMap = walletRepository.findByUserIdAndIsActiveTrue(currentUserId)
                .stream().collect(Collectors.toMap(Wallet::getId, w -> w));

        // Find category id appeared in tx list
        Set<Long> categoryIds = txs.stream()
                .map(Transaction::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Query category needed
        Map<Long, Category> categoryMap = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        return txs.stream().map((tx) ->
                TransactionMapper.toResponse(
                        tx,
                        walletMap.get(tx.getWalletId()),
                        categoryMap.get(tx.getCategoryId())
                )).toList();
    }

}
