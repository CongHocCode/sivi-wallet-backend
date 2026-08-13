package com.sivi.wallet.entity;

import com.sivi.wallet.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Bill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long payerId;

    private Long groupId; // Null if split outside any group

    @Column(nullable = false, unique = true)
    private Long transactionId; // 1-to-1 link with Payer's expense transaction

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    private String description;

    private String receiptImageUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status = BillStatus.PENDING; // PENDING, SETTLED, CANCELLED
}