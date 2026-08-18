package com.sivi.wallet.dto.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
}