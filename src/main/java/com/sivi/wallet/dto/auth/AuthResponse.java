package com.sivi.wallet.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String accessToken;
    private String tokenType;
    private String username;
}