package com.sivi.wallet.mapper;

import com.sivi.wallet.dto.transaction.TransactionRequest;
import com.sivi.wallet.dto.transaction.TransactionResponse;
import com.sivi.wallet.entity.Category;
import com.sivi.wallet.entity.Transaction;
import com.sivi.wallet.entity.Wallet;

public class TransactionMapper {

    public static Transaction toEntity(TransactionRequest request) {
        return Transaction.builder()
                .walletId(request.getWalletId())
                .categoryId(request.getCategoryId())
                .amount(request.getAmount())
                .type(request.getType())
                .note(request.getNote())
                .sourceType(request.getSourceType())
                .transactionDate(request.getTransactionDate())
                .build();
    }

    public static TransactionResponse toResponse(Transaction transaction, Wallet wallet, Category category) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .walletName(wallet != null ? wallet.getName() : null)
                .categoryId(transaction.getCategoryId())
                .categoryName(category != null ? category.getName() : null)
                .categoryIcon(category != null ? category.getIconUrl() : null)
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .note(transaction.getNote())
                .sourceType(transaction.getSourceType())
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}