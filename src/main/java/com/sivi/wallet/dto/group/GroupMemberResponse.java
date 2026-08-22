package com.sivi.wallet.dto.group;

import com.sivi.wallet.enums.GroupRole;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponse {
    private Long groupId;
    private Long userId;
    private String username;
    private String fullName;
    private Boolean isGuest;
    private GroupRole role;
    private LocalDateTime joinedAt;
}