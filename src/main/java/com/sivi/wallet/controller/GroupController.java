package com.sivi.wallet.controller;

import com.sivi.wallet.dto.common.ApiResponse;
import com.sivi.wallet.dto.group.AddMemberRequest;
import com.sivi.wallet.dto.group.GroupMemberResponse;
import com.sivi.wallet.dto.group.GroupRequest;
import com.sivi.wallet.dto.group.GroupResponse;
import com.sivi.wallet.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    ApiResponse<GroupResponse> createGroup(@RequestBody @Valid GroupRequest request) {
        return ApiResponse.success("Tạo nhóm thành công", groupService.createGroup(request));
    }

    @GetMapping
    ApiResponse<List<GroupResponse>> getGroups() {
        return ApiResponse.success("Lấy danh sách nhóm thành công", groupService.getGroups());
    }

    @PostMapping("/{groupId}/members")
    ApiResponse<GroupMemberResponse> addMember(@PathVariable Long groupId, @RequestBody AddMemberRequest request) {
        return ApiResponse.success("Thêm thành viên mới thành công", groupService.addMember(request, groupId));
    }
}
