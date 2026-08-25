package com.sivi.wallet.dto.bill;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtLedgerResponse {
    private BigDecimal totalYouOwe;
    private BigDecimal totalOwedToYou;
    private List<DebtItemResponse> debts;
}