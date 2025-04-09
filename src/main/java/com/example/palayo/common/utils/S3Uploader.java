package com.example.palayo.common.utils;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${custom.cloudfront.domain}")
    private String cloudFrontDomain;

    public List<String> uploadFiles(List<MultipartFile> files, String dir) {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String originalFilename = file.getOriginalFilename(); //ex) "example.png"
                // 디렉토리/UUID_원본 파일명(originalFilename)
                String key = dir + "/" + UUID.randomUUID() + "_" + originalFilename;

                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucket) // 버킷 이름
                        .key(key) //위에 선언된 키
                        .contentType(file.getContentType()) // image, png 등 MIME
                        .build();
                //S3업로드
                s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                //업로드된 파일의 url
                String url = cloudFrontDomain + "/" + key;
                uploadedUrls.add(url);
            } catch (IOException e) {
                // 실패한 파일명 반환
                throw new BaseException(ErrorCode.UPLOAD_FAILED, file.getOriginalFilename());
            }
        }

        return uploadedUrls;
    }

}
