package com.vipa.medlabel.service.group;

import com.vipa.medlabel.repository.ImageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.vipa.medlabel.dto.request.group.CreateGroupRequest;
import com.vipa.medlabel.dto.request.group.UpdateGroupRequest;
import com.vipa.medlabel.dto.request.image.DeleteImageRequest;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.dto.response.group.GroupSearchResult;
import com.vipa.medlabel.model.Image;
import com.vipa.medlabel.model.ImageGroup;
import com.vipa.medlabel.model.Project;
import com.vipa.medlabel.repository.ImageGroupRepository;
import org.springframework.stereotype.Service;
import com.vipa.medlabel.repository.ProjectRepository;
import com.vipa.medlabel.exception.CustomError;
import com.vipa.medlabel.exception.CustomException;
import com.vipa.medlabel.service.image.ImageService;

import lombok.AllArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
@AllArgsConstructor
public class GroupService {

    private final ImageService imageService;

    private final ImageGroupRepository imageGroupRepository;
    private final ImageRepository imageRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public SearchResult<ImageGroup> searchGroup(Integer projectId, Integer groupId, String groupName,
            String groupDescription, Integer page, Integer size) {

        Specification<ImageGroup> spec = Specification.where(null);
        if (groupId != null) {
            spec = spec
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("imageGroupId"), groupId));
        }
        if (projectId != null) {
            spec = spec
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("project").get("projectId"),
                            projectId));
        }
        if (groupName != null && !groupName.isEmpty()) {
            spec = spec
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("imageGroupName"),
                            "%" + groupName + "%"));
        }
        if (groupDescription != null && !groupDescription.isEmpty()) {
            spec = spec
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"),
                            "%" + groupDescription + "%"));
        }

        SearchResult<ImageGroup> searchResult = new GroupSearchResult();
        List<ImageGroup> selectedGroups = null;

        if (page != null && size != null && page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("imageGroupId").ascending());
            Page<ImageGroup> imageGroupPage = imageGroupRepository.findAll(spec, pageable);
            searchResult.setPageInfo(page, size, imageGroupPage.getTotalPages(), imageGroupPage.getTotalElements(),
                    imageGroupPage.isFirst(), imageGroupPage.isLast(), imageGroupPage.isEmpty());
            selectedGroups = imageGroupPage.getContent();
        } else {
            selectedGroups = imageGroupRepository.findAll(spec, Sort.by("imageGroupId").ascending());
        }

        // 对 selectedGroups 进行排序，将精确匹配的放前面，模糊匹配的放后面
        List<ImageGroup> sortedGroups = selectedGroups.stream()
                .sorted((g1, g2) -> {
                    int g1Match = 0, g2Match = 0;

                    if (groupName != null && !groupName.isEmpty()) {
                        if (g1.getImageGroupName().equals(groupName)) {
                            g1Match += 2;
                        } else if (g1.getImageGroupName().contains(groupName)) {
                            g1Match += 1;
                        }
                        if (g2.getImageGroupName().equals(groupName)) {
                            g2Match += 2;
                        } else if (g2.getImageGroupName().contains(groupName)) {
                            g2Match += 1;
                        }
                    }

                    if (groupDescription != null && !groupDescription.isEmpty()) {
                        if (g1.getDescription().equals(groupDescription)) {
                            g1Match += 2;
                        } else if (g1.getDescription().contains(groupDescription)) {
                            g1Match += 1;
                        }
                        if (g2.getDescription().equals(groupDescription)) {
                            g2Match += 2;
                        } else if (g2.getDescription().contains(groupDescription)) {
                            g2Match += 1;
                        }
                    }

                    return Integer.compare(g2Match, g1Match); // 按匹配程度降序排列
                }).toList();

        searchResult.setContent(sortedGroups);

        // 统计每个组内图片的状态
        List<List<Integer>> imageStatus = new ArrayList<>();
        for (ImageGroup group : sortedGroups) {
            List<Image> images = group.getImages();
            List<Integer> statusList = new ArrayList<>(Arrays.asList(0, 0, 0, 0));
            for (Image image : images) {
                int status = image.getStatus();
                statusList.set(status, statusList.get(status) + 1);
            }
            imageStatus.add(statusList);
        }
        ((GroupSearchResult) searchResult).setImageStatus(imageStatus);

        return searchResult;
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        Optional<ImageGroup> optionalImageGroup = imageGroupRepository.findById(groupId);
        if (optionalImageGroup.isPresent()) {
            List<Integer> imageIds = imageRepository.findImageIdByImageGroupImageGroupId(groupId);
            DeleteImageRequest deleteImageRequest = new DeleteImageRequest(imageIds);
            imageService.deleteImages(deleteImageRequest);
            imageGroupRepository.delete(optionalImageGroup.get());
        } else {
            throw new CustomException(CustomError.GROUP_NOT_FOUND);
        }
    }

    @Transactional
    public void createGroup(CreateGroupRequest createGroupRequest) {
        Project project = projectRepository.findByProjectId(createGroupRequest.getProjectId())
                .orElseThrow(() -> new CustomException(CustomError.PROJECT_NOT_FOUND));

        List<CreateGroupRequest.GroupDetail> targetGroups = createGroupRequest.getTargetGroups();

        for (CreateGroupRequest.GroupDetail targetGroup : targetGroups) {
            ImageGroup imageGroup = new ImageGroup();
            imageGroup.setImageGroupName(targetGroup.getName());
            if (targetGroup.getDescription() != null)
                imageGroup.setDescription(targetGroup.getDescription());
            imageGroup.setProject(project);

            imageGroupRepository.save(imageGroup);
        }
    }

    @Transactional
    @Retryable(retryFor = { SQLException.class,
            OptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void updateGroup(UpdateGroupRequest updateGroupRequest) {
        updateGroupRequest.getTargetGroups().forEach(groupDetail -> {
            Optional<ImageGroup> optionalGroup = imageGroupRepository.findAllByImageGroupId(groupDetail.getGroupId());
            if (optionalGroup.isPresent()) {
                ImageGroup group = optionalGroup.get();
                Project currentProject = group.getProject();
                int newProjectId = groupDetail.getProjectId();

                if (currentProject.getProjectId() != newProjectId) {
                    Project newProject = projectRepository.findById(newProjectId)
                            .orElseThrow(() -> new RuntimeException(
                                    "ProjectId " + groupDetail.getProjectId() + " not found"));
                    group.setProject(newProject);
                }

                group.setImageGroupName(groupDetail.getName());
                group.setDescription(groupDetail.getDescription());
                imageGroupRepository.save(group);
            } else {
                throw new CustomException(CustomError.GROUP_NOT_FOUND);
            }
        });
    }
}
