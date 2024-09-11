package com.vipa.medlabel.service.project;

import com.vipa.medlabel.util.DirectoryUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.vipa.medlabel.dto.request.group.CreateGroupRequest;
import com.vipa.medlabel.dto.request.group.CreateGroupRequest.GroupDetail;
import com.vipa.medlabel.dto.request.project.CreateProjectInfo;
import com.vipa.medlabel.dto.request.project.UpdateProjectInfo;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.exception.CustomError;
import com.vipa.medlabel.exception.CustomException;
import com.vipa.medlabel.model.ImageGroup;
import com.vipa.medlabel.model.ImageType;
import com.vipa.medlabel.model.Project;
import com.vipa.medlabel.model.User;
import com.vipa.medlabel.repository.ImageGroupRepository;
import com.vipa.medlabel.repository.ImageTypeRepository;
import com.vipa.medlabel.repository.ProjectRepository;
import com.vipa.medlabel.repository.UserRepository;
import com.vipa.medlabel.service.group.GroupService;
import com.vipa.medlabel.service.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final GroupService groupService;
    private final UserService userService;

    private final ProjectRepository projectRepository;
    private final ImageGroupRepository imageGroupRepository;
    private final ImageTypeRepository imageTypeRepository;

    @Value("${medlabel.projects.resource.path}")
    private String projectResourcePath;

    @Transactional
    public void createProjects(List<CreateProjectInfo> projectInfoList) {
        User user = userService.getCurrentUser();

        // 批量创建数据集,按事务处理
        for (CreateProjectInfo createProjectInfo : projectInfoList) {
            Project project = new Project();
            project.setUser(user);
            project.setProjectName(createProjectInfo.getProjectName());
            project.setDescription(createProjectInfo.getProjectDescription());

            ImageType imageType = imageTypeRepository.findByImageTypeId(createProjectInfo.getImageTypeId())
                    .orElseThrow(() -> new CustomException(CustomError.IMAGETYPE_NOT_FOUND));

            project.setImageType(imageType);
            projectRepository.save(project);

            // 在静态资源服务器上创建数据集文件夹
            String folderPath = String.format(projectResourcePath + "/projects/%d", project.getProjectId());

            Path dir = Paths.get(folderPath);
            if (Files.exists(dir)) {
                log.error(
                        "com.vipa.medlabel.service.project.createProjects: Project folder already exists: "
                                + folderPath);
            } else {
                try {

                    String perms = "rwxrwxrwx";
                    DirectoryUtil.createDirectory(dir, perms);
                } catch (IOException e) {
                    log.error(
                            "com.vipa.medlabel.service.project.createProjects: Error creating project folder: "
                                    + folderPath);
                }
            }

            // 同时创建一个默认的分组
            CreateGroupRequest createGroupRequest = new CreateGroupRequest();
            createGroupRequest.setProjectId(project.getProjectId());
            GroupDetail defaultGroup = new GroupDetail();
            defaultGroup.setName("默认组");
            defaultGroup.setDescription("默认组");
            createGroupRequest.setTargetGroups(List.of(defaultGroup));
            groupService.createGroup(createGroupRequest);
        }
    }

    @Transactional
    @Retryable(retryFor = {
            OptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void updateProjects(List<UpdateProjectInfo> projectInfoList) {
        // 批量更新数据集,按事务处理
        for (UpdateProjectInfo updateProjectInfo : projectInfoList) {
            Project project = projectRepository.findByProjectId(updateProjectInfo.getProjectId())
                    .orElseThrow(() -> new CustomException(CustomError.PROJECT_NOT_FOUND));

            project.setProjectName(updateProjectInfo.getNewProjectName());
            project.setDescription(updateProjectInfo.getNewProjectDescription());

            projectRepository.save(project);
        }
    }

    @Transactional
    public SearchResult<Project> searchProjects(Integer projectId, String projectName, Integer page, Integer size) {
        User user = userService.getCurrentUser();

        Specification<Project> spec = Specification.where(null);

        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user));
        if (projectId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("projectId"), projectId));
        }
        if (projectName != null && !projectName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("projectName"),
                    "%" + projectName + "%"));
        }

        SearchResult<Project> searchResult = new SearchResult<>();
        if (page != null && size != null && page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("projectId").ascending());
            Page<Project> projectPage = projectRepository.findAll(spec, pageable);
            searchResult.setContent(projectPage.getContent());
            searchResult.setPageInfo(page, size, projectPage.getTotalPages(), projectPage.getTotalElements(),
                    projectPage.isFirst(), projectPage.isLast(), projectPage.isEmpty());
        } else {
            searchResult.setContent(projectRepository.findAll(spec, Sort.by("projectId").ascending()));
        }
        return searchResult;
    }

    @Transactional
    public void deleteProject(Integer projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isPresent()) {
            List<ImageGroup> imageGroups = imageGroupRepository.findAllByProjectProjectId(projectId);
            // 删除所有的组
            for (ImageGroup imageGroup : imageGroups) {
                groupService.deleteGroup(imageGroup.getImageGroupId());
            }

            // 在静态资源服务器上删除数据集文件夹
            String folderPath = String.format(projectResourcePath + "/projects/%d",
                    optionalProject.get().getProjectId());

            Path dir = Paths.get(folderPath);
            if (!Files.exists(dir)) {
                log.error(
                        "com.vipa.medlabel.service.project.deleteProject: Project folder not exists: " + folderPath);
            } else {
                try {
                    DirectoryUtil.deleteDirectory(dir);
                } catch (IOException e) {
                    log.error(
                            "com.vipa.medlabel.service.project.deleteProject: Error deleting project folder: "
                                    + folderPath);
                }
            }

            projectRepository.delete(optionalProject.get());
        } else {
            throw new CustomException(CustomError.PROJECT_NOT_FOUND);
        }
    }

}
