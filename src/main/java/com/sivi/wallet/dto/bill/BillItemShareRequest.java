package com.sivi.wallet.dto.bill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillItemShareRequest {

    @NotNull(message = "ID người tham gia chia bill không được để trống")
    private Long userId; // System user / guest

    @NotNull(message = "Số tiền chia không được để trống")
    @Positive(message = "Số tiền chia phải lớn hơn 0")
    private BigDecimal amountShare;

    private Boolean isPaid = false;
}