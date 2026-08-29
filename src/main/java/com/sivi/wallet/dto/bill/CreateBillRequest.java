package com.sivi.wallet.dto.bill;

import com.sivi.wallet.enums.SourceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateBillRequest {
    private Long groupId;           // Null if there is no group related
    private Long payerId;           // Payer ID (system user / old guest)
    private String payerName;       // Payer name (new guest)
    private Long walletId;          // Required if current is payer, null if other people paid

    @NotNull(message = "Vui lòng chọn danh mục cho Bill")
    private Long categoryId;

    @NotNull(message = "Vui lòng nhập tổng số tiền bill")
    @Positive(message = "Tổng tiền bill phải lớn hơn 0")
    private BigDecimal totalAmount;

    private String description;
    private String receiptImageUrl;
    private SourceType sourceType = SourceType.MANUAL;

    @NotEmpty(message = "Danh sách người chia tiền không được để trống")
    private List<BillItemShareRequest> items;
}