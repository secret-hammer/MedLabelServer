package com.vipa.medlabel.dto.request.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProjectInfo {
    @NotNull(message = "Project Id cannot be null")
    private int projectId;

    private String newProjectName;

    @Size(max = 1000, message = "Project description must be no more than 1000 characters")
    private String newProjectDescription;

    private List<String> newCategories;

}