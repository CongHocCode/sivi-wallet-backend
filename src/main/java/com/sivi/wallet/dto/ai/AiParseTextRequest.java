package com.sivi.wallet.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiParseTextRequest {
    @NotBlank(message = "Nội dung câu nói không được để trống")
    private String text;
}
