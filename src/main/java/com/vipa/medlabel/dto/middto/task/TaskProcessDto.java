package com.vipa.medlabel.dto.middto.task;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskProcessDto {

    private Integer imageId;

    // 0:未开始 1:进行中 2:已完成 3:失败
    private Integer status;

    // 0 - 1
    private Float progress;

    // 任务执行结果
    private String result;

    private Timestamp startTime;

    private Timestamp updateTime;

    public TaskProcessDto(Integer imageId) {
        this.imageId = imageId;
        this.status = 0;
        this.progress = 0.0f;
        this.result = "";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        this.startTime = currentTimestamp;
        this.updateTime = currentTimestamp;
    }

    public void updateProgress(Integer status, Float progress) {
        this.status = status;
        this.progress = progress;
        this.updateTime = new Timestamp(System.currentTimeMillis());
    }

    public void updateResult(Integer status, Float progress, String result) {
        this.status = status;
        this.progress = progress;
        this.result = result;
        this.updateTime = new Timestamp(System.currentTimeMillis());
    }
}
