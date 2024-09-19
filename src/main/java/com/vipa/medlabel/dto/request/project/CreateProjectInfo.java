package com.vipa.medlabel.dto.request.project;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateProjectInfo {

    @NotNull(message = "Project name cannot be null")
    private String projectName;

    @NotNull(message = "Project description cannot be null")
    @Size(max = 1000, message = "Project description must be no more than 1000 characters")
    private String projectDescription;

    @NotNull(message = "Image type Id cannot be null")
    private long imageTypeId;

    @NotNull(message = "Categories cannot be null")
    private List<String> categories;
}