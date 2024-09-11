package com.vipa.medlabel.util;

import java.util.List;

import com.vipa.medlabel.model.ImageType;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ImageValidator {

    public boolean isValidImage(String imageUrl, ImageType imageType) {
        // 检查文件是否存在
        if (!Files.exists(Paths.get(imageUrl))) {
            return false;
        }

        String fileExtension = getFileExtension(imageUrl);
        String imageTypeExtension = imageType.getImageExtensions();
        List<String> allowedExtensions = JSON.parseArray(imageTypeExtension, String.class);

        // 检查文件的扩展名是否在允许的文件后缀列表中
        if (!allowedExtensions.contains(fileExtension)) {
            return false;
        }
        // 文件的扩展名在允许的文件后缀列表中，且文件存在，文件是有效的
        return true;
    }

    private String getFileExtension(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf('.') + 1);
    }
}
