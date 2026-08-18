package com.sivi.wallet.service;

import com.sivi.wallet.dto.transaction.TransactionRequest;
import com.sivi.wallet.dto.transaction.TransactionResponse;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);

}
