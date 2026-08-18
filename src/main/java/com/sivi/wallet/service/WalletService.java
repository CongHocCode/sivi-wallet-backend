package com.sivi.wallet.service;

import com.sivi.wallet.dto.wallet.WalletListResponse;
import com.sivi.wallet.dto.wallet.WalletRequest;
import com.sivi.wallet.dto.wallet.WalletResponse;
import com.sivi.wallet.dto.wallet.WalletTransferRequest;

public interface WalletService {
    WalletListResponse getAllWallets();
    WalletResponse createWallet(WalletRequest request);
    Void internalTransfer(WalletTransferRequest request);
    Void deleteWallet(Long id);
}
