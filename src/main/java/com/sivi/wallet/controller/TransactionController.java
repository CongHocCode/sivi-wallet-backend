package com.sivi.wallet.controller;

import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.dto.transaction.TransactionRequest;
import com.sivi.wallet.dto.transaction.TransactionResponse;
import com.sivi.wallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ApiResponse<TransactionResponse> createTransaction(@RequestBody @Valid TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ApiResponse.success("Tạo giao dịch thành công", response);
    }

    @GetMapping
    public ApiResponse<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long walletId) {

        return ApiResponse.success(
                "Lấy lịch sử giao dịch thành công",
                transactionService.getTransactions(month, year, walletId)
        );
    }

}
