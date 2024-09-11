package com.vipa.medlabel.dto.request.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateGroupRequest {
    @NotNull(message = "Project ID cannot be null")
    private int projectId;

    @NotNull(message = "Target groups cannot be null")
    @Valid
    private List<GroupDetail> targetGroups;

    @Data
    public static class GroupDetail {
        @NotNull(message = "Group name cannot be null")
        private String name;

        private String description;
    }
}