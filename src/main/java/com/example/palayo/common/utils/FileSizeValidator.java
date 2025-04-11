package com.example.palayo.common.utils;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileSizeValidator {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_TOTAL_SIZE = 100 * 1024 * 1024; // 100MB
    private static final int MAX_FILE_COUNT = 10;

    //전체 파일 리스트에 대한 개수, 크기, 총 용량 검사
    public void checkRequestSize(List<MultipartFile> files) {
        if (files.size() > MAX_FILE_COUNT) {
            throw new BaseException(ErrorCode.TOO_MANY_FILES,String.valueOf(files.size()));
        }

        long totalSize = 0;

        for (MultipartFile file : files) {
            long size = file.getSize();
            if (size > MAX_FILE_SIZE) {
                throw new BaseException(ErrorCode.FILE_TOO_LARGE, file.getOriginalFilename());
            }
            totalSize += size;
        }

        if (totalSize > MAX_TOTAL_SIZE) {
            throw new BaseException(ErrorCode.TOTAL_SIZE_EXCEEDED, String.valueOf(totalSize));
        }
    }
}
