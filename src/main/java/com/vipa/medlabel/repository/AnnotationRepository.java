package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {
    // Additional custom queries can be defined here
}