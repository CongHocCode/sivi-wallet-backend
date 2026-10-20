package com.sivi.wallet.dto.ai;

import com.sivi.wallet.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiParsedTransactionResponse {
    private BigDecimal amount;
    private TransactionType type;

    // Return both ID and name
    private Long walletId;
    private String walletName;
    private Long categoryId;
    private String categoryName;

    private String note;
    private String transactionDate;
}