package com.sivi.wallet.repository;

import com.sivi.wallet.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrUserIdIsNullAndIsActiveTrue(Long userId);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND (c.userId = :userId OR c.userId IS NULL) AND c.isActive = true")
    Optional<Category> findAccessibleCategory(@Param("id") Long id, @Param("userId") Long userId);
}