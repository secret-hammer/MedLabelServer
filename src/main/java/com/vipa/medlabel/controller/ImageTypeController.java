package com.vipa.medlabel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.model.ImageType;
import com.vipa.medlabel.service.imagetype.ImageTypeService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/imagetype")
public class ImageTypeController {

    private ImageTypeService imageTypeService;

    @GetMapping("/search")
    public ResponseEntity<ResponseResult<List<ImageType>>> searchImageTypes() {

        List<ImageType> imageTypeList = imageTypeService.searchAllImageTypes();

        ResponseResult<List<ImageType>> response = new ResponseResult<>(200, "Image type search successfully",
                imageTypeList);

        return ResponseEntity.ok(response);
    }
}
