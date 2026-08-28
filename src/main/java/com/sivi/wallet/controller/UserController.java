package com.sivi.wallet.controller;

import com.sivi.wallet.dto.auth.UserResponse;
import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.entity.User;
import com.sivi.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    // TODO: Create user service,...
    @GetMapping("/search")
    public ApiResponse<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        List<UserResponse> responses = users.stream()
                .map(u -> UserResponse.builder().id(u.getId()).username(u.getUsername()).fullName(u.getFullName()).isGuest(u.isGuest()).build())
                .toList();
        return ApiResponse.success("Tìm kiếm thành công", responses);
    }
}
