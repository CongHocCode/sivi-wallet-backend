package com.sivi.wallet.dto.wallet;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTransferRequest {

    @NotNull(message = "Ví gửi không được để trống")
    private Long fromWalletId;

    @NotNull(message = "Ví nhận không được để trống")
    private Long toWalletId;

    @NotNull(message = "Số tiền chuyển không được để trống")
    @DecimalMin(value = "1000.00", message = "Số tiền chuyển tối thiểu là 1.000 VNĐ")
    private BigDecimal amount;
}