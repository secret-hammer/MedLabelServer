package com.vipa.medlabel.dto.request.image;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadImageRequest {
    @NotNull(message = "Image URLs cannot be null")
    private List<String> imageUrls;

    @NotNull(message = "Image group ID cannot be null")
    private Integer imageGroupId;

    @NotNull(message = "Image type ID cannot be null")
    private Integer imageTypeId;
}
