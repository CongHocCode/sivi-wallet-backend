package com.sivi.wallet.dto.common;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String code;     // Mã nghiệp vụ (VD: "SUCCESS", "USER_EXISTS", "INVALID_CREDENTIALS")
    private String message;  // Thông báo chi tiết
    private T data;          // Dữ liệu nhả về

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}