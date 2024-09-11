package com.vipa.medlabel.dto.middto.task;

import com.vipa.medlabel.model.Image;
import com.vipa.medlabel.model.Project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateImageConvertTaskDto {

    // 由taskService创建并设置
    private String taskId;

    @NotNull(message = "ProjectId cannot be null")
    private Integer projectId;

    @NotNull(message = "Image name cannot be null")
    private String imageName;

    @NotNull(message = "Image type ID cannot be null")
    private Integer imageTypeId;

    @NotNull(message = "ImageId cannot be null")
    private Integer imageId;

    @NotNull(message = "Image URL cannot be null")
    private String imageUrl;

    public CreateImageConvertTaskDto(Project project, Image image) {
        this.projectId = project.getProjectId();
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
        this.imageName = image.getImageName();
        this.imageTypeId = project.getImageType().getImageTypeId();
    }

    public CreateImageConvertTaskDto(Integer projectId, Integer imageId, String imageUrl, String imageName,
            Integer imageTypeId) {
        this.projectId = projectId;
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.imageTypeId = imageTypeId;
    }
}
