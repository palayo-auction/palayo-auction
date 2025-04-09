package com.example.palayo.common.controller;

import com.example.palayo.common.response.Response;
import com.example.palayo.common.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileUploadController {

    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public Response<List<String>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("dir") String dir
    ) {
        List<String> urls = s3Uploader.uploadFiles(files, dir);
        return Response.of(urls);
    }
}
