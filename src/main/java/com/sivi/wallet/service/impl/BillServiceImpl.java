package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.bill.BillDetailResponse;
import com.sivi.wallet.dto.bill.BillItemShareRequest;
import com.sivi.wallet.dto.bill.BillResponse;
import com.sivi.wallet.dto.bill.CreateBillRequest;
import com.sivi.wallet.entity.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                .map(item -> BillMapper.toDetailEntity(
                        savedBill.getId(),
                        item.getUserId(),
                        item.getAmountShare(),
                        item.getUserId().equals(currentUserId)
                ))
                .toList();
        List<BillDetail> savedDetails = billDetailRepository.saveAll(billDetails);

        // Responding
        Map<Long, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<BillDetailResponse> detailResponses = savedDetails.stream()
                .map(detail -> BillMapper.toDetailResponse(detail, userMap.get(detail.getUserId())))
                .toList();

        return BillMapper.toResponse(savedBill, detailResponses, group, userMap.get(currentUserId));
    }
}
