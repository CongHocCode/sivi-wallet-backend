package com.sivi.wallet.util;

import com.sivi.wallet.entity.User;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    public static User getCurrentUser(UserRepository userRepository) {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    public static Long getCurrentUserId(UserRepository userRepository) {
        return getCurrentUser(userRepository).getId();
    }
}