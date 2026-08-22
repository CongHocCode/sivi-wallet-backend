package com.sivi.wallet.service;

import com.sivi.wallet.dto.group.AddMemberRequest;
import com.sivi.wallet.dto.group.GroupMemberResponse;
import com.sivi.wallet.dto.group.GroupRequest;
import com.sivi.wallet.dto.group.GroupResponse;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request);
    List<GroupResponse> getGroups();
    GroupMemberResponse addMember(AddMemberRequest request, Long groupId);
}
