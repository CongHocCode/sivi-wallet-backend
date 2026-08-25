package com.sivi.wallet.dto.bill;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDetailResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String username;
    private Boolean isGuest;
    private BigDecimal amountShare;
    private Boolean isPaid;
    private LocalDateTime paidAt;
}