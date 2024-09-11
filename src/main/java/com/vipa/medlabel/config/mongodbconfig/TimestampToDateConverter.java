package com.vipa.medlabel.config.mongodbconfig;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.Date;

@Component
public class TimestampToDateConverter implements Converter<Timestamp, Date> {
    @Override
    public Date convert(Timestamp source) {
        return new Date(source.getTime());
    }
}