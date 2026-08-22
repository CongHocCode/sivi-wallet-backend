package com.sivi.wallet.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupRequest {
    @NotBlank
    private String name;
}
