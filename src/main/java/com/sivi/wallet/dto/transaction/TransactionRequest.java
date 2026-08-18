package com.sivi.wallet.dto.transaction;

import com.sivi.wallet.enums.SourceType;
import com.sivi.wallet.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequest {

    @NotNull(message = "Vui lòng chọn ví thực hiện giao dịch")
    private Long walletId;

    private Long categoryId; // Null if Transfer / Debt settle type

    @NotNull(message = "Vui lòng nhập số tiền")
    @DecimalMin(value = "0.0", message = "Số tiền giao dịch không được là số âm")
    private BigDecimal amount;

    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String note;

    @NotNull(message = "Vui lòng chọn loại giao dịch (Thu/Chi)")
    private TransactionType type;

    private SourceType sourceType = SourceType.MANUAL; // Default transaction source is manual. Frontend can change to other types

    @NotNull(message = "Vui lòng chọn ngày giao dịch")
    private LocalDateTime transactionDate;
}