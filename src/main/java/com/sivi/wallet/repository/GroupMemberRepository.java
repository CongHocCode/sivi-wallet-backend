package com.sivi.wallet.repository;

import com.sivi.wallet.entity.GroupMember;
import com.sivi.wallet.entity.GroupMemberId;
import com.sivi.wallet.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findByIdGroupId(Long groupId);

    List<GroupMember> findByIdUserId(Long userId);
    boolean existsById_GroupIdAndId_UserIdAndRole(Long groupId, Long currentUserId, GroupRole role);
}