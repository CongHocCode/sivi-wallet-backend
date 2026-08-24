package com.sivi.wallet.service;

import com.sivi.wallet.dto.bill.BillResponse;
import com.sivi.wallet.dto.bill.CreateBillRequest;

public interface BillService {
    BillResponse createBill(CreateBillRequest request);
}
