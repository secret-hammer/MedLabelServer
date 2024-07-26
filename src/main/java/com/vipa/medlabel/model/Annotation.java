package com.vipa.medlabel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Annotation")
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int annotationId;

    @Column(nullable = false, length = 50)
    private String annotationName;

    @Column(nullable = false, length = 255)
    private String annotationUrl;

    @Column(nullable = false, length = 255)
    private String annotatedBy;

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdTime;

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private Timestamp updatedTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imageId", nullable = false)
    private Image image;
}
