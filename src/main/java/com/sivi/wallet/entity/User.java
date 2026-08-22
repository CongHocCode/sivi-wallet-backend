package com.sivi.wallet.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // nullable = false (Guest username = null)
    private String username;

    @Column(unique = true)
    private String email;

    private String password; // Guest does not have password

    @Column(nullable = false)
    private String fullName;

    private String avatarUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean isGuest = false;
}