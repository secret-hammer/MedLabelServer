package com.vipa.medlabel.dto.request.image;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateImageInfo {
    @NotNull(message = "Image Id cannot be null")
    private Integer imageId;

    private Integer newImageGroupId;
}
