package com.vipa.medlabel.dto.request.annotation;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UpdateAnnotationInfo {
    @NotNull(message = "Annotation Id cannot be null")
    private String annotationId;

    private String annotationName;

    private String description;

    private String annotatedBy;

    private String annotationResult;
}
