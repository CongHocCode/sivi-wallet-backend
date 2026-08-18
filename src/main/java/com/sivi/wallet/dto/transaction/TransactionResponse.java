package com.sivi.wallet.dto.transaction;

import com.sivi.wallet.enums.SourceType;
import com.sivi.wallet.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long walletId;
    private String walletName;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;     // Icon (Eg: "🍔")
    private BigDecimal amount;
    private TransactionType type;    // INCOME, EXPENSE...
    private String note;
    private SourceType sourceType;   // MANUAL, AI_VISION...
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
}