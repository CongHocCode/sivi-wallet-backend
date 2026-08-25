package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Bill;
import com.sivi.wallet.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByGroupId(Long groupId);
    List<Bill> findByPayerId(Long payerId);
    List<Bill> findByPayerIdAndStatus(Long payerId, BillStatus status);

}