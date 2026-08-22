package com.sivi.wallet.service.impl;

import com.sivi.wallet.dto.group.AddMemberRequest;
import com.sivi.wallet.dto.group.GroupMemberResponse;
import com.sivi.wallet.dto.group.GroupRequest;
import com.sivi.wallet.dto.group.GroupResponse;
import com.sivi.wallet.entity.Group;
import com.sivi.wallet.entity.GroupMember;
import com.sivi.wallet.entity.GroupMemberId;
import com.sivi.wallet.entity.User;
import com.sivi.wallet.enums.GroupRole;
import com.sivi.wallet.exception.AppException;
import com.sivi.wallet.exception.ErrorCode;
import com.sivi.wallet.mapper.GroupMapper;
import com.sivi.wallet.mapper.GroupMemberMapper;
import com.sivi.wallet.repository.GroupMemberRepository;
import com.sivi.wallet.repository.GroupRepository;
import com.sivi.wallet.repository.UserRepository;
import com.sivi.wallet.service.GroupService;
import com.sivi.wallet.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public GroupResponse createGroup(GroupRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        Group group = GroupMapper.toEntity(request);
        group.setCreatorId(currentUserId);
        Group savedGroup = groupRepository.save(group);

        GroupMember creator = GroupMember.builder()
                .id(new GroupMemberId(savedGroup.getId(), currentUserId))
                .role(GroupRole.ADMIN)
                .build();

        groupMemberRepository.save(creator);
        return GroupMapper.toResponse(savedGroup);
    }

    @Override
    public List<GroupResponse> getGroups() {
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        List<GroupMember> memberships = groupMemberRepository.findByIdUserId(currentUserId);

        List<Long> groupIds = memberships.stream().map((member) -> member.getId().getGroupId()).toList();
        List<Group> groups = groupRepository.findAllById(groupIds);
        return groups.stream().map(GroupMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(AddMemberRequest request, Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new AppException(ErrorCode.GROUP_NOT_FOUND);
        }

        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        boolean isAdmin = groupMemberRepository.existsById_GroupIdAndId_UserIdAndRole(groupId, currentUserId, GroupRole.ADMIN);
        if (!isAdmin) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Define targetUser (system user / guest)
        User targetUser;
        if (request.getUserId() != null) {
            // Check for duplicate joins
            if (groupMemberRepository.existsById(new GroupMemberId(groupId, request.getUserId()))) {
                throw new AppException(ErrorCode.ALREADY_JOINED);
            }
            targetUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        } else {
            // Create new guest user
            targetUser = userRepository.save(
                    User.builder()
                            .fullName(request.getFullName())
                            .isGuest(true)
                            .build()
            );
        }

        GroupMember member = GroupMemberMapper.toEntity(request, groupId, targetUser);
        GroupMember savedMember = groupMemberRepository.saveAndFlush(member); //Make sure joinedAt is updated

        return GroupMemberMapper.toResponse(savedMember, targetUser);
    }
}
