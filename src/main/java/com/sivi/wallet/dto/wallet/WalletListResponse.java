package com.sivi.wallet.dto.wallet;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class WalletListResponse {
    private BigDecimal totalBalance; //TODO: Currency is fixed as VND for now
    private List<WalletResponse> wallets;
}