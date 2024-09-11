package com.vipa.medlabel.model;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vipa.medlabel.config.mongodbconfig.ObjectIdDeserializer;
import com.vipa.medlabel.config.mongodbconfig.ObjectIdSerializer;

// MongoDB 文档类型，这里给出springboot的类定义进行格式限定
@Data
@Document(collection = "annotations")
public class Annotation {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId annotationId;

    private String annotationName;

    private String description;

    @NotNull(message = "Image Id is required")
    private Integer imageId;

    private Timestamp createdTime;

    private Timestamp updatedTime;

    private String annotatedBy;

    @NotNull(message = "User Id is required")
    private Integer userId; // 直接关联用户ID，避免多级关联访问

    private String annotationResult; // 标注结果
}