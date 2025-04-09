package com.example.palayo.common.utils;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Component
public class ImageExtensionValidator {

    //MIME 집합
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp"
    );

    //확장자 집합
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "webp"
    );

    public void validateImageFile(MultipartFile file) {
        // MIME 검사
        String contentType = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase();
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BaseException(ErrorCode.UNSUPPORTED_FILE_TYPE, contentType);
        }

        // 확장자 검사
        String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BaseException(ErrorCode.UNSUPPORTED_FILE_EXTENSION, extension);
        }
    }
}
