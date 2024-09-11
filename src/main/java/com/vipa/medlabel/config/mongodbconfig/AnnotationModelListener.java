package com.vipa.medlabel.config.mongodbconfig;

import java.sql.Timestamp;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.vipa.medlabel.model.Annotation;

@Component
public class AnnotationModelListener extends AbstractMongoEventListener<Annotation> {

    // 用于自动更新维护annotation的时间戳字段
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Annotation> event) {
        Annotation annotation = event.getSource();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        if (annotation.getCreatedTime() == null) {
            annotation.setCreatedTime(currentTimestamp);
        }
        annotation.setUpdatedTime(currentTimestamp);
    }
}
