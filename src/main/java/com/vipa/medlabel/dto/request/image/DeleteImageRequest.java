package com.vipa.medlabel.dto.request.image;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteImageRequest {
    @NotNull(message = "Image IDs cannot be null")
    private List<Integer> imageIds;
}
