package com.sivi.wallet.mapper;

import com.sivi.wallet.dto.wallet.WalletRequest;
import com.sivi.wallet.dto.wallet.WalletResponse;
import com.sivi.wallet.entity.Wallet;

public class WalletMapper {

    public static Wallet toEntity(WalletRequest request, Long userId) {
        return Wallet.builder()
                .userId(userId)
                .name(request.getName())
                .walletType(request.getType())
                .balance(request.getBalance())
                //.currency(request.getCurrency())
                .build();
    }

    public static WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .type(wallet.getWalletType())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}