package com.vipa.medlabel.service.annotation;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vipa.medlabel.repository.AnnotationRepository;
import com.vipa.medlabel.repository.ImageGroupRepository;
import com.vipa.medlabel.repository.ProjectRepository;
import com.vipa.medlabel.service.user.UserService;

import com.vipa.medlabel.dto.request.annotation.*;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.dto.response.annotation.DownloadAnnotationInfo;
import com.vipa.medlabel.exception.CustomError;
import com.vipa.medlabel.exception.CustomException;
import com.vipa.medlabel.model.Annotation;
import com.vipa.medlabel.model.Image;
import com.vipa.medlabel.model.ImageGroup;
import com.vipa.medlabel.model.Project;
import com.vipa.medlabel.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnotationService {

    private final UserService userService;

    private final AnnotationRepository annotationRepository;
    private final ImageGroupRepository imageGroupRepository;
    private final ProjectRepository projectRepository;

    private final MongoTemplate mongoTemplate;

    public void createAnnotations(List<CreateAnnotationInfo> annotationInfoList) {
        User user = userService.getCurrentUser();
        // 批量创建标注信息
        for (CreateAnnotationInfo createAnnotationInfo : annotationInfoList) {
            Annotation annotation = new Annotation();
            annotation.setAnnotationName(createAnnotationInfo.getAnnotationName());
            annotation.setImageId(createAnnotationInfo.getImageId());
            annotation.setDescription(createAnnotationInfo.getDescription());
            annotation.setAnnotationResult(createAnnotationInfo.getAnnotationResult());
            annotation.setUserId(user.getUserId());
            annotation.setAnnotatedBy(
                    createAnnotationInfo.getAnnotatedBy() != null ? createAnnotationInfo.getAnnotatedBy()
                            : user.getUsername());
            annotationRepository.save(annotation);
        }
    }

    public void updateAnnotations(List<UpdateAnnotationInfo> annotationInfoList) {

        // 检查AnnotationId是否存在(这个地方可以用事务来避免提前查找)
        for (UpdateAnnotationInfo updateAnnotationInfo : annotationInfoList) {
            Annotation annotation = annotationRepository
                    .findByAnnotationId(new ObjectId(updateAnnotationInfo.getAnnotationId()));
            if (annotation == null) {
                throw new CustomException(CustomError.ANNOTATION_NOT_FOUND);
            }
        }

        for (UpdateAnnotationInfo updateAnnotationInfo : annotationInfoList) {
            Annotation annotation = annotationRepository
                    .findByAnnotationId(new ObjectId(updateAnnotationInfo.getAnnotationId()));

            String newAnnotationName = updateAnnotationInfo.getAnnotationName();
            String newAnnotatedBy = updateAnnotationInfo.getAnnotatedBy();
            String newDescription = updateAnnotationInfo.getDescription();
            String newAnnotationResult = updateAnnotationInfo.getAnnotationResult();

            if (newAnnotationName != null && !newAnnotationName.isEmpty()) {
                annotation.setAnnotationName(newAnnotationName);
            }
            if (newAnnotatedBy != null && !newAnnotatedBy.isEmpty()) {
                annotation.setAnnotatedBy(newAnnotatedBy);
            }
            if (newDescription != null && !newDescription.isEmpty()) {
                annotation.setDescription(newDescription);
            }
            if (newAnnotationResult != null && !newAnnotationResult.isEmpty()) {
                annotation.setAnnotationResult(newAnnotationResult);
            }
            annotationRepository.save(annotation);
        }
    }

    public SearchResult<Annotation> searchAnnotations(Integer imageId, String annotationId, String annotationName,
            String annotatedBy, Integer page, Integer size) {
        User user = userService.getCurrentUser();
        Query query = new Query();

        // 添加查询条件
        query.addCriteria(Criteria.where("userId").is(user.getUserId()));
        if (imageId != null) {
            query.addCriteria(Criteria.where("imageId").is(imageId));
        }
        if (annotationId != null && !annotationId.isEmpty()) {
            query.addCriteria(Criteria.where("annotationId").is(new ObjectId(annotationId)));
        }
        if (annotationName != null && !annotationName.isEmpty()) {
            query.addCriteria(Criteria.where("annotationName").regex(annotationName));
        }
        if (annotatedBy != null && !annotatedBy.isEmpty()) {
            query.addCriteria(Criteria.where("annotatedBy").is(annotatedBy));
        }

        SearchResult<Annotation> searchResult = new SearchResult<>();

        if (page != null && size != null && page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by("annotationId").ascending());

            query.with(pageable);

            List<Annotation> annotations = mongoTemplate.find(query, Annotation.class);

            // 获取总记录数
            long total = mongoTemplate.count(query, Annotation.class);

            Page<Annotation> annotationPage = new PageImpl<>(annotations, pageable,
                    total);

            searchResult.setContent(annotations);

            searchResult.setPageInfo(page, size, annotationPage.getTotalPages(),
                    annotationPage.getTotalElements(),
                    annotationPage.isFirst(), annotationPage.isLast(), annotationPage.isEmpty());
        } else {
            searchResult.setContent(mongoTemplate.find(query, Annotation.class));
        }

        return searchResult;
    }

    public void deleteAnnotation(String annotationId) {
        Annotation annotation = annotationRepository.findByAnnotationId(new ObjectId(annotationId));
        if (annotation == null) {
            throw new CustomException(CustomError.ANNOTATION_NOT_FOUND);
        }
        annotationRepository.delete(annotation);
    }

    public Map<String, Map<String, Map<String, List<DownloadAnnotationInfo>>>> downloadByProjects(
            List<Integer> projectIds) {
        Map<String, Map<String, Map<String, List<DownloadAnnotationInfo>>>> resultMap = new HashMap<>();
        for (Integer projectId : projectIds) {
            Optional<Project> optionalProject = projectRepository.findByProjectId(projectId);
            if (!optionalProject.isPresent()) {
                throw new CustomException(CustomError.PROJECT_NOT_FOUND);
            }
            Project project = optionalProject.get();
            List<ImageGroup> imageGroups = project.getImageGroups();
            List<Integer> imageGroupIds = new ArrayList<>();
            for (ImageGroup imageGroup : imageGroups) {
                imageGroupIds.add(imageGroup.getImageGroupId());
            }
            Map<String, Map<String, List<DownloadAnnotationInfo>>> imageGroupAnnotationInfoMap;
            imageGroupAnnotationInfoMap = downloadByImageGroups(imageGroupIds);
            resultMap.put(project.getProjectId() + "&&&" + project.getProjectName(), imageGroupAnnotationInfoMap);
        }
        return resultMap;
    }

    public Map<String, Map<String, List<DownloadAnnotationInfo>>> downloadByImageGroups(List<Integer> imageGroupIds) {
        Map<String, Map<String, List<DownloadAnnotationInfo>>> resultMap = new HashMap<>();
        for (Integer imageGroupId : imageGroupIds) {
            Optional<ImageGroup> optionaImageGroup = imageGroupRepository.findAllByImageGroupId(imageGroupId);

            if (!optionaImageGroup.isPresent()) {
                throw new CustomException(CustomError.GROUP_NOT_FOUND);
            }

            ImageGroup imageGroup = optionaImageGroup.get();
            List<Image> images = imageGroup.getImages();
            Map<String, List<DownloadAnnotationInfo>> imageAnnotationInfoMap = new HashMap<>();
            for (Image image : images) {
                List<Annotation> annotations = annotationRepository.findByImageId(image.getImageId());
                List<DownloadAnnotationInfo> downloadAnnotationInfos = new ArrayList<>();
                for (Annotation annotation : annotations) {
                    downloadAnnotationInfos.add(new DownloadAnnotationInfo(annotation));
                }
                imageAnnotationInfoMap.put(image.getImageUrl() + "&&&" + image.getImageName(), downloadAnnotationInfos);
            }
            resultMap.put(imageGroup.getImageGroupId() + "&&&" + imageGroup.getImageGroupName(),
                    imageAnnotationInfoMap);
        }
        return resultMap;
    }
}
