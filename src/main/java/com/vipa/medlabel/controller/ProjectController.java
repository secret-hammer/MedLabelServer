package com.vipa.medlabel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vipa.medlabel.dto.request.project.CreateProjectInfo;
import com.vipa.medlabel.dto.request.project.UpdateProjectInfo;
import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.model.Project;
import com.vipa.medlabel.service.project.ProjectService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ResponseResult<Object>> createProjects(
            @Valid @RequestBody List<CreateProjectInfo> projectInfoList) {

        // 批量创建数据集
        projectService.createProjects(projectInfoList);

        ResponseResult<Object> response = new ResponseResult<>(200, "Project created successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseResult<Object>> updateProjects(
            @Valid @RequestBody List<UpdateProjectInfo> projectInfoList) {

        // 批量更新数据集
        projectService.updateProjects(projectInfoList);

        ResponseResult<Object> response = new ResponseResult<>(200, "Projects updated successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseResult<SearchResult<Project>>> searchProjects(
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        SearchResult<Project> searchResult = projectService.searchProjects(projectId, projectName, page, size);

        ResponseResult<SearchResult<Project>> response = new ResponseResult<>(200, "Project search successfully",
                searchResult);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseResult<Object>> deleteProject(
            @RequestParam(required = true) Integer projectId) {

        projectService.deleteProject(projectId);
        ResponseResult<Object> response = new ResponseResult<>(200, "Group delete process completed");

        return ResponseEntity.ok(response);
    }

}
