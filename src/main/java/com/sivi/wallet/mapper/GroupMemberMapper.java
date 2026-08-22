package com.sivi.wallet.mapper;

import com.sivi.wallet.dto.group.AddMemberRequest;
import com.sivi.wallet.dto.group.GroupMemberResponse;
import com.sivi.wallet.entity.GroupMember;
import com.sivi.wallet.entity.GroupMemberId;
import com.sivi.wallet.entity.User;
import com.sivi.wallet.enums.GroupRole;

public class GroupMemberMapper {

    public static GroupMember toEntity(AddMemberRequest request, Long groupId, User targetUser)  {
        GroupRole assignedRole = targetUser.isGuest()
                ? GroupRole.MEMBER
                : (request.getRole() != null ? request.getRole() : GroupRole.MEMBER);

        return GroupMember.builder()
                .id(new GroupMemberId(groupId, targetUser.getId()))
                .role(assignedRole)
                .build();
    }

    public static GroupMemberResponse toResponse(GroupMember member, User user) {
        return GroupMemberResponse.builder()
                .groupId(member.getId().getGroupId())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .isGuest(user.isGuest())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}