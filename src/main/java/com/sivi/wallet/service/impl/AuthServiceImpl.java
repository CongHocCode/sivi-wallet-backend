package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.auth.AuthRequest;
import com.sivi.wallet.dto.auth.AuthResponse;
import com.sivi.wallet.entity.User;
import com.sivi.wallet.entity.Wallet;
import com.sivi.wallet.enums.WalletType;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.repository.WalletRepository;
import com.sivi.wallet.security.JwtTokenProvider;
import com.sivi.wallet.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .isGuest(false)
                .build();

        User savedUser = userRepository.save(user);

        // Auto-create a default Cash wallet for newly registered user
        Wallet defaultWallet = Wallet.builder()
                .userId(savedUser.getId())
                .name("Ví Tiền Mặt")
                .walletType(WalletType.CASH)
                .balance(BigDecimal.ZERO)
                .currency("VND")
                .isActive(true)
                .build();
        walletRepository.save(defaultWallet);

        String token = tokenProvider.generateToken(savedUser);
        return new AuthResponse(savedUser.getId(), token, "Bearer", savedUser.getUsername());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(user.getId(), token, "Bearer", user.getUsername());
    }
}