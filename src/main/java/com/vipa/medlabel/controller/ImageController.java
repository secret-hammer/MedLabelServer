package com.vipa.medlabel.controller;

import com.vipa.medlabel.dto.request.image.DeleteImageRequest;
import com.vipa.medlabel.dto.request.image.UpdateImageInfo;
import com.vipa.medlabel.dto.request.image.UploadImageFolderRequest;
import com.vipa.medlabel.dto.request.image.SearchImageRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vipa.medlabel.dto.request.image.UploadImageRequest;
import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.model.Image;
import com.vipa.medlabel.service.image.ImageService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload/image")
    public ResponseEntity<ResponseResult<List<String>>> uploadImages(
            @Valid @RequestBody UploadImageRequest uploadImageRequest) {

        List<String> results = imageService.uploadImages(uploadImageRequest);
        ResponseResult<List<String>> response = new ResponseResult<>(200, "Image upload process started",
                results);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/folder")
    public ResponseEntity<ResponseResult<List<String>>> uploadImageFolder(
            @Valid @RequestBody UploadImageFolderRequest uploadImageFolderRequest) {

        List<String> results = imageService.uploadImageFolder(uploadImageFolderRequest);
        ResponseResult<List<String>> response = new ResponseResult<>(200,
                " Image folder upload process started",
                results);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseResult<Object>> updateImages(
            @Valid @RequestBody List<UpdateImageInfo> updateImageRequest) {

        imageService.updateImage(updateImageRequest);
        ResponseResult<Object> response = new ResponseResult<>(200, "Images updated completed");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseResult<SearchResult<Image>>> searchImages(
            @RequestParam(required = false) Integer imageId,
            @RequestParam(required = true) Integer imageGroupId,
            @RequestParam(required = false) String imageName,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Integer imageTypeId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        SearchResult<Image> images = imageService.searchImages(imageId, imageGroupId, imageName, imageUrl, imageTypeId,
                page, size);

        ResponseResult<SearchResult<Image>> response = new ResponseResult<>(200, "Image search successfully", images);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseResult<List<String>>> deleteImages(
            @Valid @RequestBody DeleteImageRequest deleteImageRequest) {

        imageService.deleteImages(deleteImageRequest);
        ResponseResult<List<String>> response = new ResponseResult<>(200, "Image delete process completed");

        return ResponseEntity.ok(response);
    }
}
