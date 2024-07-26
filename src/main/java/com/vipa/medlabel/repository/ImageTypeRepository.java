package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageTypeRepository extends JpaRepository<ImageType, Integer> {
    // Additional custom queries can be defined here
}