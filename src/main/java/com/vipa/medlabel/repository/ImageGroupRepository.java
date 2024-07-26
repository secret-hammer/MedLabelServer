package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.ImageGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageGroupRepository extends JpaRepository<ImageGroup, Integer> {
    // Additional custom queries can be defined here
}