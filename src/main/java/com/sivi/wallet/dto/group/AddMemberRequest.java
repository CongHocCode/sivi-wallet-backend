package com.sivi.wallet.dto.group;

import com.sivi.wallet.enums.GroupRole;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {

    //Who got invited
    private Long userId;
    private String fullName;
    private GroupRole role = GroupRole.MEMBER;

    @AssertTrue(message = "Phải cung cấp userId hoặc tên người dùng khách")
    private boolean isValidMember() {
        return userId != null || (fullName != null && !fullName.trim().isEmpty());
    }

}
