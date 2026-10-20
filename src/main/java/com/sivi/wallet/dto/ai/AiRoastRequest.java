package com.sivi.wallet.dto.ai;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AiRoastRequest {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private String topCategory;
}
