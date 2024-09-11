package com.vipa.medlabel.dto.request.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateGroupRequest {
    @NotNull(message = "Target groups cannot be null")
    private List<GroupDetail> targetGroups;

    @Data
    public static class GroupDetail {
        @NotNull(message = "Group ID cannot be null")
        private int groupId;

        @NotNull(message = "Group ID cannot be null")
        private int projectId;

        @NotNull(message = "Group name cannot be null")
        private String name;

        @NotNull(message = "Group description cannot be null")
        private String description;
    }
}