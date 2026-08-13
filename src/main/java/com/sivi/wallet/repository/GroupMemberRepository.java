package com.sivi.wallet.repository;

import com.sivi.wallet.entity.GroupMember;
import com.sivi.wallet.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findByIdGroupId(Long groupId);

    List<GroupMember> findByIdUserId(Long userId);
}