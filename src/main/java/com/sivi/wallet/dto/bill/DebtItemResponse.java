package com.sivi.wallet.dto.bill;

import com.sivi.wallet.enums.DebtType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtItemResponse {
    private Long billDetailId;
    private Long billId;
    private String description;
    private Long otherUserId;         // creditor / debtor ID
    private String otherUserName;     // Their name
    private Boolean otherUserIsGuest; // Guest
    private BigDecimal amount;
    private DebtType type;            // "YOU_OWE" or "OWES_YOU"
    private LocalDateTime createdAt;
}