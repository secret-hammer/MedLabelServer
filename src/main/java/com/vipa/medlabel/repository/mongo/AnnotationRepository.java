package com.vipa.medlabel.repository.mongo;

import com.vipa.medlabel.model.Annotation;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnnotationRepository extends MongoRepository<Annotation, ObjectId> {
    Annotation findByAnnotationId(ObjectId annotationId);

    List<Annotation> findByImageId(Integer imageId);
}