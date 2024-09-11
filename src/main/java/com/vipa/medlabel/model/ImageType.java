package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ImageType")
public class ImageType {
    @Id
    private Integer imageTypeId;

    @Column(nullable = false, length = 50)
    private String imageTypeName;

    @Column(nullable = false, length = 500)
    private String imageExtensions;

    @OneToMany(mappedBy = "imageType")
    @JsonIgnore
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "imageType")
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();
}
