package com.vipa.medlabel.repository.jpa;

import com.vipa.medlabel.model.ImageType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageTypeRepository extends JpaRepository<ImageType, Integer> {

    Optional<ImageType> findByImageTypeId(long imageTypeId);
}
