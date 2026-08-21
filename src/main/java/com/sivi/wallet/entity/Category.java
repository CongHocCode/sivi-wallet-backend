package com.sivi.wallet.entity;

import com.sivi.wallet.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // NULL = System, NOT NULL = Custom User

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type; // INCOME, EXPENSE

    private String iconUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}