package com.vipa.medlabel.dto.request.project;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProjectInfo {
    @NotNull(message = "Project Id cannot be null")
    private int projectId;

    private String newProjectName;

    @Size(max = 1000, message = "Project description must be no more than 1000 characters")
    private String newProjectDescription;

}