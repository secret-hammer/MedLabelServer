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
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectId;

    @Column(nullable = false, length = 50)
    private String projectName;

    @Column(nullable = true, length = 2000)
    private String description = "N/A";

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdTime;

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private Timestamp updatedTime;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ImageGroup> imageGroups = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "imageTypeId", nullable = false)
    private ImageType imageType;
}
