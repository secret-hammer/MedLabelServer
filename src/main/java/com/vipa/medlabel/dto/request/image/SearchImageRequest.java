package com.vipa.medlabel.dto.request.image;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchImageRequest {
    private Integer imageId;

    @NotNull(message = "Image Id cannot be null")
    private Integer imageGroupId;

    private String imageName;

    private String imageUrl;

    private Integer imageTypeId;
}
