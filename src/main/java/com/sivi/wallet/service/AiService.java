package com.sivi.wallet.service;

import com.sivi.wallet.dto.ai.AiParsedTransactionResponse;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

public interface AiService {

    // Text parsing
    AiParsedTransactionResponse parseNaturalLanguage(String text, Long currentUserId);

    // OCR receipt scan
    String scanReceipt(MultipartFile file, String userInstruction, Long currentUserId);

    // Financial Assistance roast
    String generateRoast(BigDecimal income, BigDecimal expense, String topCategory);
}