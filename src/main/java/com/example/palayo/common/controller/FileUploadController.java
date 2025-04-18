package com.example.palayo.common.controller;

import com.example.palayo.common.dto.ImageDeleteRequest;
import com.example.palayo.common.response.Response;
import com.example.palayo.common.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class FileUploadController {
    private final S3Uploader s3Uploader;

    @PostMapping(value = "/{itemId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<List<String>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @PathVariable Long itemId
    ) {
        List<String> urls = s3Uploader.uploadFiles(files, String.valueOf(itemId));
        return Response.of(urls);
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<List<String>> replaceItemImageFiles(
            @RequestPart List<MultipartFile> files,
            @RequestPart List<String> originalUrls
    ) {
        List<String> updatedUrls = s3Uploader.updateFiles(originalUrls, files);
        return Response.of(updatedUrls);
    }
}
