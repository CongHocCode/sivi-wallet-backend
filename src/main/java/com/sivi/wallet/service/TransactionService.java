package com.sivi.wallet.service;

import com.sivi.wallet.dto.transaction.TransactionRequest;
import com.sivi.wallet.dto.transaction.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    List<TransactionResponse> getTransactions(Integer month, Integer year, Long walletId);
    void deleteTransaction(Long id);
}
