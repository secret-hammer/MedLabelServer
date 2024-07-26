package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String imageName;

    @Column(nullable = false)
    private int status = 0;

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdTime;

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private Timestamp updatedTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imageGroupId", nullable = false)
    private ImageGroup imageGroup;

    @OneToMany(mappedBy = "image")
    private List<Annotation> annotations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "imageTypeId", nullable = false)
    private ImageType imageType;
}
