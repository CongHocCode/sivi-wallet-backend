package com.sivi.wallet.dto.wallet;

import com.sivi.wallet.enums.WalletType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRequest {

    @NotBlank(message = "Tên ví không được để trống")
    @Size(max = 50, message = "Tên ví không được vượt quá 50 ký tự")
    private String name;

    @NotNull(message = "Loại ví không được để trống") // Enum -> @NotNull
    private WalletType type; // CASH, BANK, E_WALLET

    @NotNull(message = "Số dư ban đầu không được để trống")
    @DecimalMin(value = "0.0", message = "Số dư ban đầu không được là số âm")
    private BigDecimal balance;

    private String currency = "VND";
}