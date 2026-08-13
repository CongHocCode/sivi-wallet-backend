package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByGroupId(Long groupId);
    List<Bill> findByPayerId(Long payerId);
}