package com.sivi.wallet.service;

import com.sivi.wallet.dto.auth.AuthRequest;
import com.sivi.wallet.dto.auth.AuthResponse;
import com.sivi.wallet.dto.auth.UserResponse;
import org.springframework.stereotype.Service;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
    UserResponse getCurrentUser();
}
