package com.sivi.wallet.controller;

import com.sivi.wallet.dto.ai.AiParseTextRequest;
import com.sivi.wallet.dto.ai.AiParsedTransactionResponse;
import com.sivi.wallet.dto.ai.AiRoastRequest;
import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.service.AiService;
import com.sivi.wallet.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final UserRepository userRepository;

    @PostMapping("/parse-text")
    public ApiResponse<AiParsedTransactionResponse> parseText(@Valid @RequestBody AiParseTextRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        AiParsedTransactionResponse response = aiService.parseNaturalLanguage(request.getText(), currentUserId);
        return ApiResponse.success("Phân tích giao dịch thành công", response);
    }

    /**
     * URL: POST /api/v1/ai/receipt-ocr
     * Consumes: multipart/form-data
     */
    @PostMapping(value = "/receipt-ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> scanReceipt(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "instruction", required = false) String instruction) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        String jsonResult = aiService.scanReceipt(file, instruction, currentUserId);
        return ApiResponse.success("Bóc tách hóa đơn thành công", jsonResult);
    }

    /**
     * URL: POST /api/v1/ai/coach-roast
     */
    @PostMapping("/coach-roast")
    public ApiResponse<String> coachRoast(@Valid @RequestBody AiRoastRequest request) {
        String roastResult = aiService.generateRoast(
                request.getTotalIncome(),
                request.getTotalExpense(),
                request.getTopCategory()
        );
        return ApiResponse.success("Tạo lời cố vấn thành công", roastResult);
    }
}