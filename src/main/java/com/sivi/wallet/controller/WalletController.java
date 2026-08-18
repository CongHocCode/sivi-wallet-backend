package com.sivi.wallet.controller;

import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.dto.wallet.WalletListResponse;
import com.sivi.wallet.dto.wallet.WalletRequest;
import com.sivi.wallet.dto.wallet.WalletResponse;
import com.sivi.wallet.dto.wallet.WalletTransferRequest;
import com.sivi.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    public ApiResponse<WalletListResponse> getAllWallets() {
        WalletListResponse response = walletService.getAllWallets();
        return ApiResponse.success("Lấy danh sách ví thành công", response);
    }

    @PostMapping
    public ApiResponse<WalletResponse> createWallet(@Valid @RequestBody WalletRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return ApiResponse.success("Tạo ví mới thành công", response);
    }

    @PostMapping("/transfer")
    public ApiResponse<Void> internalTransfer(@Valid @RequestBody WalletTransferRequest request) {
        return ApiResponse.success("Chuyển tiền sang ví mới thành công", walletService.internalTransfer(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWallet(@PathVariable Long id) {
        return ApiResponse.success("Xóa ví thành công", walletService.deleteWallet(id));
    }


}
