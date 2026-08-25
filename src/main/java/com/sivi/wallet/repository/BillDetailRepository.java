package com.sivi.wallet.repository;

import com.sivi.wallet.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {
    List<BillDetail> findByBillId(Long billId);

    List<BillDetail> findByUserIdAndIsPaidFalse(Long userId);
    List<BillDetail> findByBillIdInAndIsPaidFalse(Collection<Long> billIds);
}