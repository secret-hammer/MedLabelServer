package com.vipa.medlabel.repository;

import com.vipa.medlabel.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer>, JpaSpecificationExecutor<Image> {
    // Additional custom queries can be defined here
    List<Image> findByImageGroupImageGroupId(Integer imageGroupId);

    @Query("SELECT i.imageId FROM Image i WHERE i.imageGroup.imageGroupId = :groupId")
    List<Integer> findImageIdByImageGroupImageGroupId(Integer groupId);

    Image findByImageId(Integer imageId);
}