package com.sivi.wallet.controller;

import com.sivi.wallet.dto.bill.BillResponse;
import com.sivi.wallet.dto.bill.CreateBillRequest;
import com.sivi.wallet.dto.bill.DebtLedgerResponse;
import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    // Create split bill
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BillResponse> createBill(@Valid @RequestBody CreateBillRequest request) {
        return ApiResponse.success("Tạo hóa đơn chia tiền thành công", billService.createBill(request));
    }

    @GetMapping("/debts")
    public ApiResponse<DebtLedgerResponse> getDebts() {
        return ApiResponse.success("Lấy danh sách sổ nợ thành công", billService.getDebts());
    }

    @PostMapping("/settle/{billDetailId}")
    public ApiResponse<Void> settleDebt(@PathVariable Long billDetailId, @RequestParam Long walletId) {

        billService.settleDebt(billDetailId, walletId);
        return ApiResponse.success("Xác nhận tất toán nợ thành công", null);
    }

    @GetMapping
    public ApiResponse<List<BillResponse>> getMyBills() {
        return ApiResponse.success("Lấy danh sách hóa đơn thành công", billService.getMyBills());
    }
}