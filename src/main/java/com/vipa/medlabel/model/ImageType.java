package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ImageType")
public class ImageType {
    @Id
    private int imageTypeId;

    @Column(nullable = false, length = 50)
    private String imageTypeName;

    @Column(nullable = false, length = 500)
    private String imageExtensions;

    @OneToMany(mappedBy = "imageType")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "imageType")
    private List<Project> projects = new ArrayList<>();
}
