package com.sivi.wallet.dto.group;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupResponse {
    private Long id;
    private String name;
    private Long creatorId;
    private LocalDateTime createdAt;
}
