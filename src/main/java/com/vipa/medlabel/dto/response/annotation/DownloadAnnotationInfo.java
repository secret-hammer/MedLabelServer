package com.vipa.medlabel.dto.response.annotation;

import com.vipa.medlabel.model.Annotation;

import lombok.Data;

@Data
public class DownloadAnnotationInfo {
    private String annotationName;
    private String annotationResult;
    private String annotatedBy;

    public DownloadAnnotationInfo(String annotationName, String annotationResult, String annotatedBy) {
        this.annotationName = annotationName;
        this.annotationResult = annotationResult;
        this.annotatedBy = annotatedBy;
    }

    public DownloadAnnotationInfo(Annotation annotation) {
        this.annotationName = annotation.getAnnotationName();
        this.annotationResult = annotation.getAnnotationResult();
        this.annotatedBy = annotation.getAnnotatedBy();
    }
}