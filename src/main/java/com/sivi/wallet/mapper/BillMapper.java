package com.sivi.wallet.mapper;

import com.sivi.wallet.dto.bill.BillDetailResponse;
import com.sivi.wallet.dto.bill.BillResponse;
import com.sivi.wallet.dto.bill.CreateBillRequest;
import com.sivi.wallet.entity.Bill;
import com.sivi.wallet.entity.BillDetail;
import com.sivi.wallet.entity.Group;
import com.sivi.wallet.entity.User;
import com.sivi.wallet.enums.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillMapper {

    public static Bill toEntity(CreateBillRequest request, Long payerId, Long transactionId) {
        return Bill.builder()
                .payerId(payerId)
                .groupId(request.getGroupId())
                .transactionId(transactionId)
                .totalAmount(request.getTotalAmount())
                .description(request.getDescription())
                .receiptImageUrl(request.getReceiptImageUrl())
                .status(BillStatus.PENDING)
                .build();
    }

    public static BillDetail toDetailEntity(Long billId, Long userId, BigDecimal amountShare, boolean isPaid) {
        return BillDetail.builder()
                .billId(billId)
                .userId(userId)
                .amountShare(amountShare)
                .isPaid(isPaid)
                .paidAt(isPaid ? LocalDateTime.now() : null)
                .build();
    }

    public static BillDetailResponse toDetailResponse(BillDetail detail, User user) {
        return BillDetailResponse.builder()
                .id(detail.getId())
                .userId(detail.getUserId())
                .fullName(user != null ? user.getFullName() : null)
                .username(user != null ? user.getUsername() : null)
                .isGuest(user != null && user.isGuest())
                .amountShare(detail.getAmountShare())
                .isPaid(detail.getIsPaid())
                .paidAt(detail.getPaidAt())
                .build();
    }

    public static BillResponse toResponse(Bill bill, List<BillDetailResponse> details, Group group, User payer) {
        return BillResponse.builder()
                .id(bill.getId())
                .payerId(bill.getPayerId())
                .payerName(payer != null ? payer.getFullName() : null)
                .groupId(bill.getGroupId())
                .groupName(group != null ? group.getName() : null)
                .transactionId(bill.getTransactionId())
                .totalAmount(bill.getTotalAmount())
                .description(bill.getDescription())
                .receiptImageUrl(bill.getReceiptImageUrl())
                .status(bill.getStatus())
                .createdAt(bill.getCreatedAt())
                .details(details)
                .build();
    }
}