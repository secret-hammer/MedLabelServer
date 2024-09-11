package com.vipa.medlabel.service.image;

import com.vipa.medlabel.dto.middto.task.CreateImageConvertTaskDto;
import com.vipa.medlabel.dto.request.image.DeleteImageRequest;
import com.vipa.medlabel.dto.request.image.UpdateImageInfo;
import com.vipa.medlabel.dto.request.image.UploadImageFolderRequest;
import com.vipa.medlabel.model.*;
import com.vipa.medlabel.repository.ImageGroupRepository;
import com.vipa.medlabel.repository.ImageRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.vipa.medlabel.dto.request.image.UploadImageRequest;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.exception.CustomError;
import com.vipa.medlabel.exception.CustomException;
import com.vipa.medlabel.repository.ImageTypeRepository;
import com.vipa.medlabel.service.task.TaskService;
import com.vipa.medlabel.service.user.UserService;
import com.vipa.medlabel.util.DirectoryUtil;
import com.vipa.medlabel.util.ImageValidator;

import java.io.IOException;
import java.nio.file.*;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
    private final TaskService taskService;

    private final ImageTypeRepository imageTypeRepository;
    private final ImageValidator imageValidator;
    private final ImageGroupRepository imageGroupRepository;
    private final ImageRepository imageRepository;

    @Value("${medlabel.projects.resource.path}")
    private String projectResourcePath;

    @Transactional
    @Retryable(retryFor = {
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public List<String> uploadImages(@Valid UploadImageRequest uploadImageRequest) {
        List<String> results = new ArrayList<>();
        ImageGroup imageGroup = imageGroupRepository.findById(uploadImageRequest.getImageGroupId())
                .orElseThrow(() -> new CustomException(CustomError.GROUP_NOT_FOUND));
        ImageType imageType = imageTypeRepository.findById(uploadImageRequest.getImageTypeId())
                .orElseThrow(() -> new CustomException(CustomError.IMAGETYPE_NOT_FOUND));

        // 检查上传的图片类型是否与项目的图片类型匹配
        if (imageGroup.getProject().getImageType().getImageTypeId() != imageType.getImageTypeId()) {
            throw new CustomException(CustomError.IMAGETYPE_NOT_MATCH);
        }

        for (String imageUrl : uploadImageRequest.getImageUrls()) {
            if (imageValidator.isValidImage(imageUrl, imageType)) {
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setImageName(getImageName());
                image.setImageGroup(imageGroup);
                image.setImageType(imageType);
                image.setStatus(0);
                imageRepository.save(image);

                // 如果是病理图，创建静态资源服务器图片文件夹，单独存放这个图片的deepzoom格式图片
                if (imageType.getImageTypeId().equals(3)) {
                    createImageFolder(image);
                }

                // CreateImageConvertTaskDto createImageConvertTaskDto = new
                // CreateImageConvertTaskDto(
                // image.getImageGroup().getProject(), image);
                // taskService.submitImageConvertTask(createImageConvertTaskDto);

                results.add("Success!");
            } else {
                results.add("Failed: " + imageUrl + " - Invalid image format or file not found.");
            }
        }
        return results;
    }

    @Transactional
    @Retryable(retryFor = {
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public List<String> uploadImageFolder(@Valid UploadImageFolderRequest uploadImageFolderRequest) {
        List<String> results = new ArrayList<>();
        ImageGroup imageGroup = imageGroupRepository.findById(uploadImageFolderRequest.getImageGroupId())
                .orElseThrow(() -> new CustomException(CustomError.GROUP_NOT_FOUND));
        ImageType imageType = imageTypeRepository.findById(uploadImageFolderRequest.getImageTypeId())
                .orElseThrow(() -> new CustomException(CustomError.IMAGETYPE_NOT_FOUND));

        // 检查上传的图片类型是否与项目的图片类型匹配
        if (!imageGroup.getProject().getImageType().getImageTypeId().equals(imageType.getImageTypeId())) {
            throw new CustomException(CustomError.IMAGETYPE_NOT_MATCH);
        }

        String folderPath = uploadImageFolderRequest.getImageFolderUrl();
        Path dir = Paths.get(folderPath);
        if (Files.notExists(dir) || !Files.isDirectory(dir)) {
            throw new CustomException(CustomError.INVALID_FOLDER_URL);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String imageUrl = entry.toString();
                    if (imageValidator.isValidImage(imageUrl, imageType)) {
                        Image image = new Image();
                        image.setImageUrl(imageUrl);
                        image.setImageName(getImageName());
                        image.setImageGroup(imageGroup);
                        image.setImageType(imageType);
                        image.setStatus(0);
                        imageRepository.save(image);

                        // 如果是病理图，创建静态资源服务器图片文件夹，单独存放这个图片的deepzoom格式图片
                        if (imageType.getImageTypeId().equals(3)) {
                            createImageFolder(image);
                        }

                        CreateImageConvertTaskDto createImageConvertTaskDto = new CreateImageConvertTaskDto(
                                image.getImageGroup().getProject(), image);
                        taskService.submitImageConvertTask(createImageConvertTaskDto);

                        results.add("Success!");
                    } else {
                        results.add("Failed: " + imageUrl + " - Invalid image format or file not found.");
                    }
                }
            }
        } catch (IOException e) {
            throw new CustomException(CustomError.READ_FOLDER_ERROR);
        }
        return results;
    }

    @Transactional
    public SearchResult<Image> searchImages(Integer imageId, Integer imageGroupId, String imageName, String imageUrl,
            Integer imageTypeId, Integer page, Integer size) {
        Specification<Image> spec = Specification.where(null);

        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("imageGroup").get("imageGroupId"), imageGroupId));

        if (imageId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("imageId"), imageId));
        }

        if (imageTypeId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("imageType").get("imageTypeId"), imageTypeId));
        }

        if (imageName != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("imageName"),
                    "%" + imageName + "%"));
        }

        if (imageUrl != null) {
            spec = spec.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("imageUrl"), "%" + imageUrl + "%"));
        }

        SearchResult<Image> searchResult = new SearchResult<>();
        List<Image> selectedImages = null;

        if (page != null && size != null && page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("imageId").ascending());
            Page<Image> imagePage = imageRepository.findAll(spec, pageable);
            searchResult.setPageInfo(page, size, imagePage.getTotalPages(), imagePage.getTotalElements(),
                    imagePage.isFirst(), imagePage.isLast(), imagePage.isEmpty());
            selectedImages = imagePage.getContent();
        } else {
            selectedImages = imageRepository.findAll(spec);
        }

        // 对 selectedImages 进行排序，将精确匹配的放前面，模糊匹配的放后面
        List<Image> sortedImages = selectedImages.stream()
                .sorted((i1, i2) -> {
                    int i1Match = 0;
                    int i2Match = 0;

                    if (imageName != null && !imageName.isEmpty()) {
                        if (i1.getImageName().equals(imageName)) {
                            i1Match += 2;
                        } else if (i1.getImageName().contains(imageName)) {
                            i1Match += 1;
                        }
                        if (i2.getImageName().equals(imageName)) {
                            i2Match += 2;
                        } else if (i2.getImageName().contains(imageName)) {
                            i2Match += 1;
                        }
                    }

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        if (i1.getImageUrl().equals(imageUrl)) {
                            i1Match += 2;
                        } else if (i1.getImageUrl().contains(imageUrl)) {
                            i1Match += 1;
                        }
                        if (i2.getImageUrl().equals(imageUrl)) {
                            i2Match += 2;
                        } else if (i2.getImageUrl().contains(imageUrl)) {
                            i2Match += 1;
                        }
                    }

                    return Integer.compare(i2Match, i1Match); // 按匹配程度降序排列
                }).toList();

        searchResult.setContent(sortedImages);
        return searchResult;
    }

    @Transactional
    @Retryable(retryFor = {
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void updateImage(List<UpdateImageInfo> updateImageRequest) {
        for (UpdateImageInfo updateImageInfo : updateImageRequest) {
            Image image = imageRepository.findById(updateImageInfo.getImageId())
                    .orElseThrow(() -> new CustomException(CustomError.IMAGE_ID_NOT_FOUND));

            if (updateImageInfo.getNewImageGroupId() > 0) {
                ImageGroup imageGroup = imageGroupRepository.findById(updateImageInfo.getNewImageGroupId())
                        .orElseThrow(() -> new CustomException(CustomError.GROUP_NOT_FOUND));

                if (!imageGroup.getProject().getProjectId().equals(image.getImageGroup().getProject().getProjectId())) {
                    throw new CustomException(CustomError.CROSS_PROJECT_MOVE_ERROR);
                }

                image.setImageGroup(imageGroup);
            }

            // 保存更新后的实体
            imageRepository.save(image);
        }
    }

    @Transactional
    public void deleteImages(@Valid DeleteImageRequest deleteImageRequest) {
        List<Image> images = new ArrayList<>();
        for (Integer imageId : deleteImageRequest.getImageIds()) {
            // 如果找不到不要报错
            Image image = imageRepository.findById(imageId).orElse(null);
            if (image == null)
                throw new CustomException(CustomError.IMAGE_ID_NOT_FOUND);
            images.add(image);
        }
        for (Image image : images) {
            // 如果是病理图，删除静态资源服务器图片文件夹; 非病理图，删除图片文件
            if (image.getImageType().getImageTypeId().equals(3)) {
                deleteImageFolder(image);
            } else {
                deleteImageFile(image);
            }
            imageRepository.delete(image);
        }
    }

    private void deleteImageFolder(Image image) {
        Project project = image.getImageGroup().getProject();
        String folderPath = String.format(projectResourcePath + "/projects/%d/%s", project.getProjectId(),
                image.getImageName());

        Path dir = Paths.get(folderPath);
        if (Files.notExists(dir)) {
            log.error("com.vipa.medlabel.service.image.deleteImageFolder: Image folder not found: " + folderPath);
        } else {
            try {
                DirectoryUtil.deleteDirectory(dir);
            } catch (IOException e) {
                log.error(
                        "com.vipa.medlabel.service.image.deleteImageFolder: Error deleting image folder: "
                                + folderPath);
            }
        }
    }

    private void deleteImageFile(Image image) {
        Project project = image.getImageGroup().getProject();
        // 目前系统规定所有图片都是png格式
        String imagePath = String.format(projectResourcePath + "/projects/%d/%s", project.getProjectId(),
                image.getImageName() + ".png");

        Path imageFile = Paths.get(imagePath);
        if (Files.notExists(imageFile)) {
            log.error("com.vipa.medlabel.service.image.deleteImageFolder: Image folder not found: " + imagePath);
        } else {
            try {
                Files.delete(imageFile);
            } catch (IOException e) {
                log.error(
                        "com.vipa.medlabel.service.image.deleteImageFolder: Error deleting image folder: "
                                + imagePath);
            }
        }
    }

    private void createImageFolder(Image image) {
        Project project = image.getImageGroup().getProject();
        String folderPath = String.format(projectResourcePath + "/projects/%d/%s", project.getProjectId(),
                image.getImageName());

        Path dir = Paths.get(folderPath);
        if (Files.exists(dir)) {
            log.error("com.vipa.medlabel.service.image.createImageFolder: Image folder already exists: " + folderPath);
        } else {
            try {
                String perms = "rwxrwxrwx";
                DirectoryUtil.createDirectory(dir, perms);
            } catch (IOException e) {
                log.error(
                        "com.vipa.medlabel.service.image.createImageFolder: Error creating image folder: "
                                + folderPath);
            }
        }
    }

    // 生成一个全局唯一的图片名
    private String getImageName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
