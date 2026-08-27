package com.sivi.wallet.service;

import com.sivi.wallet.dto.bill.BillResponse;
import com.sivi.wallet.dto.bill.CreateBillRequest;
import com.sivi.wallet.dto.bill.DebtLedgerResponse;

import java.util.List;

public interface BillService {
    BillResponse createBill(CreateBillRequest request);
    DebtLedgerResponse getDebts();
    void settleDebt(Long billDetailId, Long walletId);
    List<BillResponse> getMyBills();
}
