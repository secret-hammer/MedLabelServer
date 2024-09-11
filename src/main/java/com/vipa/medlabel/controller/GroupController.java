package com.vipa.medlabel.controller;

import com.vipa.medlabel.dto.request.group.CreateGroupRequest;
import com.vipa.medlabel.dto.request.group.UpdateGroupRequest;
import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.model.ImageGroup;
import com.vipa.medlabel.service.group.GroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private GroupService groupService;

    @GetMapping("/search")
    public ResponseEntity<ResponseResult<SearchResult<ImageGroup>>> searchGroup(
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String groupDescription,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        SearchResult<ImageGroup> groups = groupService.searchGroup(projectId, groupId, groupName, groupDescription,
                page, size);

        ResponseResult<SearchResult<ImageGroup>> response = new ResponseResult<>(200,
                "Group information retrieved successfully", groups);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseResult<Object>> createGroup(
            @Valid @RequestBody CreateGroupRequest createGroupRequest) {

        groupService.createGroup(createGroupRequest);
        ResponseResult<Object> response = new ResponseResult<>(200, "Group created successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseResult<Object>> updateGroup(
            @Valid @RequestBody UpdateGroupRequest updateGroupRequest) {

        groupService.updateGroup(updateGroupRequest);
        ResponseResult<Object> response = new ResponseResult<>(200, "Groups updated successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseResult<Object>> deleteGroups(
            @RequestParam(required = true) Integer groupId) {

        groupService.deleteGroup(groupId);
        ResponseResult<Object> response = new ResponseResult<>(200, "Group delete process completed");

        return ResponseEntity.ok(response);
    }
}
