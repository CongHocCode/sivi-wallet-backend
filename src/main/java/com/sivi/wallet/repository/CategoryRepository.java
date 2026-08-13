package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrUserIdIsNullAndIsActiveTrue(Long userId);
}