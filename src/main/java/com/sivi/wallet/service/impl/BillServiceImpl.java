package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.bill.*;
import com.sivi.wallet.entity.*;
import com.sivi.wallet.enums.BillStatus;
import com.sivi.wallet.enums.DebtType;
import com.sivi.wallet.enums.TransactionType;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.mapper.BillMapper;
import com.sivi.wallet.repository.*;
import com.sivi.wallet.service.BillService;
import com.sivi.wallet.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TransactionRepository transactionRepository;


    @Override
    @Transactional
    public BillResponse createBill(CreateBillRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);

        // Check for wallet's availability
        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getWalletId(), currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        if (wallet.getBalance().compareTo(request.getTotalAmount()) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // Check category existence
        if (!categoryRepository.existsAccessibleCategory(request.getCategoryId(), currentUserId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // Collect all bill participant IDs
        Set<Long> allUserIds = request.getItems().stream()
                .map(BillItemShareRequest::getUserId).collect(Collectors.toSet());
        allUserIds.add(currentUserId);

        // Group & user validity
        Group group = null;
        if (request.getGroupId() != null) {
            group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

            if (groupMemberRepository.countMembersInGroup(request.getGroupId(), allUserIds) != allUserIds.size()) {
                throw new AppException(ErrorCode.MEMBER_NOT_IN_GROUP);
            }
        } else {
            if (userRepository.countByIdIn(allUserIds) != allUserIds.size()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
        }

        // Check if total share equal total amount
        BigDecimal totalShare = request.getItems().stream()
                .map(BillItemShareRequest::getAmountShare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalShare.compareTo(request.getTotalAmount()) != 0) {
            throw new AppException(ErrorCode.BAD_REQUEST); // "Tổng tiền chia không khớp!"
        }

        // Set new balance for wallet
        wallet.setBalance(wallet.getBalance().subtract(request.getTotalAmount()));

        // Create transaction
        Transaction tx = Transaction.builder()
                .walletId(wallet.getId())
                .categoryId(request.getCategoryId())
                .amount(request.getTotalAmount())
                .type(TransactionType.EXPENSE)
                .note("Thanh toán Bill: " + (request.getDescription() != null ? request.getDescription() : "Chia tiền"))
                .sourceType(request.getSourceType())
                .isDeleted(false)
                .transactionDate(LocalDateTime.now())
                .build();
        Transaction savedTx = transactionRepository.save(tx);

        // Create Bill
        Bill bill = BillMapper.toEntity(request, currentUserId, savedTx.getId());
        Bill savedBill = billRepository.save(bill);

        // Create Bill Details (Payer -> isPaid = true, paidAt = now)
        List<BillDetail> billDetails = request.getItems().stream()
                .map(item -> {
                    boolean isPaid = item.getUserId().equals(currentUserId) || Boolean.TRUE.equals(item.getIsPaid());
                    return BillMapper.toDetailEntity(
                            savedBill.getId(),
                            item.getUserId(),
                            item.getAmountShare(),
                            isPaid
                    );
                })
                .toList();
        List<BillDetail> savedDetails = billDetailRepository.saveAll(billDetails);

        // Update Bill status if all members have paid
        boolean isAllPaid = savedDetails.stream().allMatch(BillDetail::getIsPaid);
        if (isAllPaid) {
            savedBill.setStatus(BillStatus.SETTLED);
        }

        // Responding
        Map<Long, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<BillDetailResponse> detailResponses = savedDetails.stream()
                .map(detail -> BillMapper.toDetailResponse(detail, userMap.get(detail.getUserId())))
                .toList();

        return BillMapper.toResponse(savedBill, detailResponses, group, userMap.get(currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public DebtLedgerResponse getDebts() {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);

        // YOU_OWE
        List<BillDetail> youOweDetails = billDetailRepository.findByUserIdAndIsPaidFalse(currentUserId);
        Set<Long> youOweBillIds = youOweDetails.stream().map(BillDetail::getBillId).collect(Collectors.toSet());
        Map<Long, Bill> youOweBillsMap = billRepository.findAllById(youOweBillIds).stream()
                .collect(Collectors.toMap(Bill::getId, b -> b));

        // OWES_YOU
        List<Bill> myPayerBills = billRepository.findByPayerIdAndStatus(currentUserId, BillStatus.PENDING);
        List<Long> myBillIds = myPayerBills.stream().map(Bill::getId).toList();
        Map<Long, Bill> myBillsMap = myPayerBills.stream().collect(Collectors.toMap(Bill::getId, b -> b));

        List<BillDetail> oweYouDetails = myBillIds.isEmpty() ? List.of()
                : billDetailRepository.findByBillIdInAndIsPaidFalse(myBillIds).stream()
                .filter(d -> !d.getUserId().equals(currentUserId)) // Skip payer (self)
                .toList();

        // Get counterpart name
        Set<Long> otherUserIds = new HashSet<>();
        youOweBillsMap.values().forEach(b -> otherUserIds.add(b.getPayerId()));
        oweYouDetails.forEach(d -> otherUserIds.add(d.getUserId()));

        Map<Long, User> userMap = userRepository.findAllById(otherUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // Create debt details list
        List<DebtItemResponse> debtItems = new ArrayList<>();

        // Map YOU_OWE
        for (BillDetail detail : youOweDetails) {
            Bill bill = youOweBillsMap.get(detail.getBillId());
            if (bill != null && !bill.getPayerId().equals(currentUserId)) {
                User payer = userMap.get(bill.getPayerId());
                debtItems.add(DebtItemResponse.builder()
                        .billDetailId(detail.getId())
                        .billId(bill.getId())
                        .description(bill.getDescription())
                        .otherUserId(bill.getPayerId())
                        .otherUserName(payer != null ? payer.getFullName() : "N/A")
                        .otherUserIsGuest(payer != null && payer.isGuest())
                        .amount(detail.getAmountShare())
                        .type(DebtType.YOU_OWE)
                        .createdAt(bill.getCreatedAt())
                        .build());
            }
        }

        // Map OWES_YOU
        for (BillDetail detail : oweYouDetails) {
            Bill bill = myBillsMap.get(detail.getBillId());
            User debtor = userMap.get(detail.getUserId());
            debtItems.add(DebtItemResponse.builder()
                    .billDetailId(detail.getId())
                    .billId(detail.getBillId())
                    .description(bill != null ? bill.getDescription() : "Chia tiền")
                    .otherUserId(detail.getUserId())
                    .otherUserName(debtor != null ? debtor.getFullName() : "N/A")
                    .otherUserIsGuest(debtor != null && debtor.isGuest())
                    .amount(detail.getAmountShare())
                    .type(DebtType.OWES_YOU)
                    .createdAt(bill != null ? bill.getCreatedAt() : detail.getPaidAt())
                    .build());
        }

        // Sum
        BigDecimal totalYouOwe = debtItems.stream()
                .filter(item -> item.getType() == DebtType.YOU_OWE)
                .map(DebtItemResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOwedToYou = debtItems.stream()
                .filter(item -> item.getType() == DebtType.OWES_YOU)
                .map(DebtItemResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DebtLedgerResponse.builder()
                .totalYouOwe(totalYouOwe)
                .totalOwedToYou(totalOwedToYou)
                .debts(debtItems)
                .build();
    }
}
