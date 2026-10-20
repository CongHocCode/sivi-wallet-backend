package com.sivi.wallet.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivi.wallet.dto.ai.AiParsedTransactionResponse;
import com.sivi.wallet.entity.Category;
import com.sivi.wallet.entity.Wallet;
import com.sivi.wallet.enums.CategoryType;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.repository.CategoryRepository;
import com.sivi.wallet.repository.WalletRepository;
import com.sivi.wallet.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiServiceImpl implements AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-3.5-flash-lite}")
    private String model;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    private final RestClient restClient = RestClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public AiParsedTransactionResponse parseNaturalLanguage(String text, Long currentUserId) {
        log.info("Parsing natural language text for userId: {}", currentUserId);

        List<Map<String, Object>> walletsData = walletRepository.findByUserIdAndIsActiveTrue(currentUserId)
                .stream()
                .map(w -> Map.<String, Object>of("id", w.getId(), "name", w.getName()))
                .toList();

        List<Category> accessibleCategories = categoryRepository.findByUserIdOrUserIdIsNullAndIsActiveTrue(currentUserId);

        List<Map<String, Object>> expenseCategories = accessibleCategories.stream()
                .filter(c -> c.getType() == CategoryType.EXPENSE)
                .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
                .toList();

        List<Map<String, Object>> incomeCategories = accessibleCategories.stream()
                .filter(c -> c.getType() == CategoryType.INCOME)
                .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
                .toList();

        String currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        try {
            String walletsJson = objectMapper.writeValueAsString(walletsData);
            String expenseCategoriesJson = objectMapper.writeValueAsString(expenseCategories);
            String incomeCategoriesJson = objectMapper.writeValueAsString(incomeCategories);

            String prompt = String.format("""
                            You are a Vietnamese Natural Language Transaction Parser for personal finance.
                            CURRENT REFERENCE DATETIME: %s (Timezone: GMT+7 Asia/Ho_Chi_Minh).
                            
                            USER DATABASE CONTEXT:
                            - Available Wallets: %s
                            - Available Expense Categories: %s
                            - Available Income Categories: %s
                            
                            TASK: Parse the following text into structured JSON.
                            Input Text: "%s"
                            
                            STRICT RULES:
                            1. Transaction "type":
                               - "INCOME": if receiving money, salary, bonus, lương, thưởng, bán đồ...
                               - "EXPENSE": if spending, buying, ăn uống, đi chợ, đổ xăng, thanh toán...
                            2. Match "categoryId" & "categoryName":
                               - If "EXPENSE", pick the most suitable category STRICTLY from Available Expense Categories.
                               - If "INCOME", pick the most suitable category STRICTLY from Available Income Categories.
                               - Fill both "categoryId" and "categoryName" accurately matching the selected object.
                            3. Match "walletId" & "walletName":
                               - Match the wallet mentioned in text with Available Wallets.
                               - If no wallet mentioned, select the first available wallet in the list.
                               - Fill both "walletId" and "walletName".
                            4. "transactionDate": Calculate relative time ("2h sáng", "hôm qua", "trưa nay") based on CURRENT REFERENCE DATETIME. Format: YYYY-MM-DDTHH:mm.
                            5. Amount: Convert colloquial numbers ("45k" -> 45000, "1 triệu" -> 1000000, "500 ngàn" -> 500000).
                            6. Output ONLY raw JSON matching schema. No markdown backticks.
                            
                            SCHEMA:
                            {
                              "amount": integer,
                              "type": "EXPENSE" or "INCOME",
                              "walletId": integer,
                              "walletName": "string",
                              "categoryId": integer,
                              "categoryName": "string",
                              "note": "string",
                              "transactionDate": "YYYY-MM-DDTHH:mm"
                            }
                            """,
                    currentDateTime, walletsJson, expenseCategoriesJson, incomeCategoriesJson, text
            );

            String rawJson = callGemini(prompt, null, null);
            return objectMapper.readValue(rawJson, AiParsedTransactionResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse natural language transaction", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String scanReceipt(MultipartFile file, String userInstruction, Long currentUserId) {
        log.info("Scanning receipt for userId: {}, hasInstruction: {}", currentUserId, userInstruction != null);

        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

            List<Category> accessibleCategories = categoryRepository.findByUserIdOrUserIdIsNullAndIsActiveTrue(currentUserId);
            List<String> expenseCategories = accessibleCategories.stream()
                    .filter(c -> c.getType() == CategoryType.EXPENSE)
                    .map(Category::getName).toList();
            String expenseCategoriesStr = String.join(", ", expenseCategories);

            List<String> walletNames = walletRepository.findByUserIdAndIsActiveTrue(currentUserId)
                    .stream().map(Wallet::getName).toList();
            String availableWalletsStr = walletNames.isEmpty() ? "Tiền mặt" : String.join(", ", walletNames);

            String currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            String prompt = String.format("""
                            You are an Intelligent Financial Receipt OCR and Expense Allocation Engine for Vietnamese receipts.
                            CURRENT REFERENCE TIME: %s (Timezone: GMT+7 Asia/Ho_Chi_Minh).
                            
                            USER SPECIAL INSTRUCTION: "%s"
                            AVAILABLE EXPENSE CATEGORIES: [%s]
                            AVAILABLE WALLETS: [%s]
                            
                            TASK:
                            1. Extract line items: [{ "itemName": "string", "quantity": 1, "totalPrice": integer }]. Exclude 0đ promotional gift items if requested.
                            2. Select "category": Choose the single most appropriate category STRICTLY from: [%s].
                            3. Detect "wallet": If user mentions a payment wallet in instruction (e.g. "bằng MoMo", "quẹt thẻ"), pick from: [%s]. Otherwise set to null.
                            4. Mathematical Expense Allocation for "totalAmount":
                               - IF SPECIAL INSTRUCTION IS "None" or empty: Set "totalAmount" to the printed final receipt total.
                               - IF SPECIAL INSTRUCTION CONTAINS SPLIT/EXCLUSION REQUESTS (e.g., "chia đôi", "tính riêng", "trừ món X"):
                                 * STEP A: Identify items belonging exclusively to the user (private items).
                                 * STEP B: Subtract exclusive items from total to find the shared pool amount: (ReceiptTotal - ExclusiveItems).
                                 * STEP C: Split the shared pool among participants: (SharedPool / numberOfPeople).
                                 * STEP D: Add user's exclusive items: (UserSharedPortion + UserExclusiveItems).
                                 * YOU MUST SET THIS FINAL COMPUTED NUMBER AS "totalAmount". DO NOT return the raw receipt total.
                            5. In "note": Write a concise 1-sentence Vietnamese explanation of the exact math formula applied.
                            6. Output ONLY raw JSON matching schema. No markdown code blocks.
                            
                            FEW-SHOT REASONING EXAMPLE:
                            - Receipt Total: 105,000đ. User Instruction: "chia đôi với bạn, 2 lon nước 18k tính riêng cho tôi, bỏ quà 0đ".
                            - Math: (105,000 - 18,000) / 2 = 43,500đ. Final total = 43,500 + 18,000 = 61,500đ.
                            - Output JSON: { "totalAmount": 61500, "note": "Chia đôi hóa đơn (105k - 18k)/2 + 18k nước riêng = 61.500đ", ... }
                            
                            SCHEMA:
                            {
                              "merchantName": "string or null",
                              "transactionDate": "YYYY-MM-DDTHH:mm or null",
                              "totalAmount": integer,
                              "category": "string (strictly from available categories)",
                              "wallet": "string or null",
                              "note": "string",
                              "items": [
                                { "itemName": "string", "quantity": 1, "totalPrice": integer }
                              ]
                            }
                            """,
                    currentDateTime,
                    userInstruction != null && !userInstruction.isBlank() ? userInstruction : "None",
                    expenseCategoriesStr,
                    availableWalletsStr,
                    expenseCategoriesStr,
                    availableWalletsStr
            );

            return callGemini(prompt, base64Image, mimeType);
        } catch (IOException e) {
            log.error("Failed to read uploaded receipt image file bytes", e);
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public String generateRoast(BigDecimal income, BigDecimal expense, String topCategory) {
        log.info("Generating AI financial roast. Income: {}, Expense: {}, TopCategory: {}", income, expense, topCategory);

        String prompt = String.format("""
                        Đóng vai một Cố Vấn Tài Chính SIVI AI cực kỳ hài hước, phũ phàng, châm biếm và dí dỏm.
                        Dữ liệu tài chính tháng này của người dùng:
                        - Tổng thu nhập: %s VNĐ
                        - Tổng chi tiêu: %s VNĐ
                        - Danh mục ngốn nhiều tiền nhất: %s
                        
                        YÊU CẦU:
                        - Hãy đưa ra đúng 2 câu nhận xét cực mặn bằng tiếng Việt, vừa nhắc nhở vừa mang tính giải trí châm biếm thói tiêu xài hoang phí.
                        - Nếu chi tiêu > thu nhập, hãy cảnh báo nguy cơ cạp đất ăn cuối tháng một cách hài hước.
                        - Chỉ trả về văn bản tiếng Việt thuần túy, tuyệt đối không dùng định dạng markdown hay ngoặc kép thừa.
                        """,
                income != null ? income : BigDecimal.ZERO,
                expense != null ? expense : BigDecimal.ZERO,
                topCategory != null ? topCategory : "Chi tiêu chung"
        );

        return callGemini(prompt, null, null);
    }

    private String callGemini(String promptText, String base64Data, String mimeType) {
        String targetUrl = String.format("%s/%s:generateContent?key=%s", apiUrl, model, apiKey);

        Map<String, Object> partText = Map.of("text", promptText);
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(partText);

        if (base64Data != null && mimeType != null) {
            Map<String, Object> inlineData = Map.of(
                    "mime_type", mimeType,
                    "data", base64Data
            );
            parts.add(Map.of("inline_data", inlineData));
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("role", "user", "parts", parts))
        );

        try {
            String rawResponse = restClient.post()
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(rawResponse);
            String content = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("");

            return content.replaceAll("```json|```", "").trim();
        } catch (Exception e) {
            log.error("Gemini API call failed. Check API key, quota, or network connection.", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}