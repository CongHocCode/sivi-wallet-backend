package com.sivi.wallet.dto.bill;

import com.sivi.wallet.enums.BillStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private Long id;
    private Long payerId;
    private String payerName;
    private Long groupId;
    private String groupName;
    private Long transactionId;
    private BigDecimal totalAmount;
    private String description;
    private String receiptImageUrl;
    private BillStatus status;
    private LocalDateTime createdAt;
    private List<BillDetailResponse> details;
}