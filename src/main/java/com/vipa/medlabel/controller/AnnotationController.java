package com.vipa.medlabel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vipa.medlabel.dto.request.annotation.CreateAnnotationInfo;
import com.vipa.medlabel.dto.request.annotation.UpdateAnnotationInfo;
import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.dto.response.annotation.DownloadAnnotationInfo;
import com.vipa.medlabel.model.Annotation;
import com.vipa.medlabel.service.annotation.AnnotationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@RequestMapping("/annotation")
public class AnnotationController {

    private AnnotationService annotationService;

    @PostMapping("/create")
    public ResponseEntity<ResponseResult<Object>> createAnnotations(
            @Valid @RequestBody List<CreateAnnotationInfo> annotationInfoList) {

        // 批量创建数据集
        annotationService.createAnnotations(annotationInfoList);

        ResponseResult<Object> response = new ResponseResult<>(200, "Annotation created successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseResult<Object>> updateAnnotations(
            @Valid @RequestBody List<UpdateAnnotationInfo> annotationInfoList) {

        // 批量更新数据集
        annotationService.updateAnnotations(annotationInfoList);

        ResponseResult<Object> response = new ResponseResult<>(200, "Annotations updated successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseResult<SearchResult<Annotation>>> searchAnnotations(
            @RequestParam(required = false) Integer imageId,
            @RequestParam(required = false) String annotationId,
            @RequestParam(required = false) String annotationName,
            @RequestParam(required = false) String annotatedBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        SearchResult<Annotation> searchResult = annotationService.searchAnnotations(imageId, annotationId,
                annotationName, annotatedBy, page, size);

        ResponseResult<SearchResult<Annotation>> response = new ResponseResult<>(200, "Annotation search successfully",
                searchResult);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseResult<Object>> deleteAnnotation(
            @RequestParam(required = true) String annotationId) {

        annotationService.deleteAnnotation(annotationId);
        ResponseResult<Object> response = new ResponseResult<>(200, "Annotation delete successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/project")
    public ResponseEntity<ResponseResult<Object>> downloadProjectAnnotation(
            @RequestParam(required = true) List<Integer> projectIds) {

        Map<String, Map<String, Map<String, List<DownloadAnnotationInfo>>>> result = annotationService
                .downloadByProjects(projectIds);
        ResponseResult<Object> response = new ResponseResult<>(200,
                "Project annotation information download successfully", result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/group")
    public ResponseEntity<ResponseResult<Object>> downloadImageGourpAnnotation(
            @RequestParam(required = true) List<Integer> imageGroupIds) {

        Map<String, Map<String, List<DownloadAnnotationInfo>>> annotationList = annotationService
                .downloadByImageGroups(imageGroupIds);
        ResponseResult<Object> response = new ResponseResult<>(200,
                "ImageGroup annotation information download successfully", annotationList);

        return ResponseEntity.ok(response);
    }
}
