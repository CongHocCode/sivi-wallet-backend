package com.sivi.wallet.dto.wallet;

import com.sivi.wallet.enums.WalletType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {
    private Long id;
    private String name;
    private WalletType type;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
}