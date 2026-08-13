package com.sivi.wallet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long billId;

    @Column(nullable = false)
    private Long userId; // Participant of the split bill (System User / Guest)

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amountShare;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPaid = false;

    private LocalDateTime paidAt;

    private Long settlementTransactionId; // Connect to settlement transaction
}