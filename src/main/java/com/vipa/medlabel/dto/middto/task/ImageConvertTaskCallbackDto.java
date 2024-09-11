package com.vipa.medlabel.dto.middto.task;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageConvertTaskCallbackDto {
    @NotNull(message = "Task ID is required")
    private String taskId;

    @NotNull(message = "Image ID is required")
    private Integer imageId;

    // 0:未开始 1:进行中 2:已完成 3:失败
    @NotNull(message = "Status is required")
    private Integer status;

    // 0 - 1
    @NotNull(message = "Progress is required")
    private Float progress;

    // 任务执行结果
    private String result;
}
