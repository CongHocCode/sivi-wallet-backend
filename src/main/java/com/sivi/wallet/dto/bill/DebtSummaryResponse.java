package com.sivi.wallet.dto.bill;

import com.sivi.wallet.enums.DebtType;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtSummaryResponse {
    private Long billDetailId;
    private Long billId;
    private String description;
    private Long otherUserId;
    private String otherUserName;
    private Boolean otherUserIsGuest;
    private BigDecimal amount;
    private DebtType type; // "YOU_OWE" (I owe them) or "OWES_YOU" (They owe me)
    private Boolean isPaid;
}