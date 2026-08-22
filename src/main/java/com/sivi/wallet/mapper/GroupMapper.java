package com.sivi.wallet.mapper;

import com.sivi.wallet.dto.group.GroupRequest;
import com.sivi.wallet.dto.group.GroupResponse;
import com.sivi.wallet.entity.Group;

public class GroupMapper {
    public static Group toEntity(GroupRequest request) {
        return Group.builder()
                .name(request.getName())
                .build();
    }

    public static GroupResponse toResponse (Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .creatorId(group.getCreatorId())
                .name(group.getName())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
