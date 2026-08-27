package com.sivi.wallet.controller;

import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.entity.Category;
import com.sivi.wallet.repository.CategoryRepository;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    //TODO: implement category service ?
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<List<Category>> getCategories() {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        List<Category> categories = categoryRepository.findByUserIdOrUserIdIsNullAndIsActiveTrue(currentUserId);
        return ApiResponse.success("Lấy danh mục thành công", categories);
    }
}