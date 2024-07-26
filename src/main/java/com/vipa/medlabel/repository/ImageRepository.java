package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    // Additional custom queries can be defined here
}