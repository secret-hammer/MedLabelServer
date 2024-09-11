package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @Column(nullable = false, unique = true, length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String imageName;

    // 0:未处理， 1:处理中， 2:处理完成， 3:处理失败
    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdTime;

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private Timestamp updatedTime;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "imageGroupId", nullable = false)
    private ImageGroup imageGroup;

    @ManyToOne
    @JoinColumn(name = "imageTypeId", nullable = false)
    private ImageType imageType;

    @Version
    @JsonIgnore
    private Integer version;
}
