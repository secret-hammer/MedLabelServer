package com.vipa.medlabel.service.imagetype;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vipa.medlabel.model.ImageType;
import com.vipa.medlabel.repository.ImageTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageTypeService {
    private final ImageTypeRepository imageTypeRepository;

    public List<ImageType> searchAllImageTypes() {
        return imageTypeRepository.findAll();
    }
}
