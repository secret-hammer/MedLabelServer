package com.vipa.medlabel.dto.request.annotation;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAnnotationInfo {
    @NotNull(message = "Annotation name cannot be null")
    private String annotationName;

    @NotNull(message = "imageId cannot be null")
    private Integer imageId;

    private String description;

    private String annotatedBy;

    private String annotationResult;
}