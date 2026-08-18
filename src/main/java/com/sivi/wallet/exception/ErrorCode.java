package com.sivi.wallet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS("2000", "Thao tác thành công"),
    USER_ALREADY_EXISTS("4001", "Tên đăng nhập hoặc email đã tồn tại trong hệ thống"),
    INVALID_CREDENTIALS("4002", "Tên đăng nhập hoặc mật khẩu không chính xác"),
    WALLET_NOT_FOUND("4003", "Không tìm thấy ví tiền yêu cầu"),
    INSUFFICIENT_BALANCE("4004", "Số dư trong ví không đủ để thực hiện giao dịch"),
    UNAUTHORIZED("4005", "Bạn không có quyền thực hiện thao tác này hoặc phiên đăng nhập đã hết hạn"),
    BAD_REQUEST("4006", "Yêu cầu không hợp lệ"),
    CATEGORY_NOT_FOUND("4007", "Không tìm thấy danh mục yêu cầu" ),
    INTERNAL_SERVER_ERROR("5000", "Lỗi hệ thống nội bộ, vui lòng thử lại sau");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}