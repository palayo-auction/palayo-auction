package com.example.palayo.common.util;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class S3UploaderTest {

    @InjectMocks
    private S3Uploader s3Uploader;

    @Mock
    private S3Client s3Client;

    @Mock
    private ImageExtensionValidator extensionValidator;

    @Mock
    private FileSizeValidator sizeValidator;

    private final String BUCKET = "test-bucket";
    private final String CLOUDFRONT_DOMAIN = "https://cdn.example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        s3Uploader = new S3Uploader(s3Client, extensionValidator, sizeValidator);

        // reflection으로 @Value 주입
        ReflectionTestUtils.setField(s3Uploader, "bucket", BUCKET);
        ReflectionTestUtils.setField(s3Uploader, "cloudFrontDomain", CLOUDFRONT_DOMAIN);

    }


    @Test
    void uploadFiles_정상작동하면_CloudFrontURL반환() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

        List<String> result = s3Uploader.uploadFiles(List.of(file), "test-dir");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).startsWith(CLOUDFRONT_DOMAIN + "/test-dir/");
    }

    @Test
    void uploadFiles_확장자_검증실패시_예외() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "invalid".getBytes());
        doThrow(new BaseException(ErrorCode.UNSUPPORTED_FILE_TYPE, file.getContentType()))
                .when(extensionValidator).validateImageFile(file);

        assertThatThrownBy(() -> s3Uploader.uploadFiles(List.of(file), "dir"))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNSUPPORTED_FILE_TYPE);
    }

    @Test
    void uploadFiles_파일크기초과시_예외발생() {
        MockMultipartFile file = new MockMultipartFile("file", "large.png", "image/png", new byte[0]);

        doThrow(new BaseException(ErrorCode.FILE_TOO_LARGE, file.getOriginalFilename()))
                .when(sizeValidator).checkRequestSize(List.of(file));

        assertThatThrownBy(() -> s3Uploader.uploadFiles(List.of(file), "dir"))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FILE_TOO_LARGE);
    }


    @Test
    void uploadFiles_업로드실패시_예외발생() throws IOException {
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenThrow(IOException.class);

        assertThatThrownBy(() -> s3Uploader.uploadFiles(List.of(file), "dir"))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UPLOAD_FAILED);
    }

    @Test
    void delete_정상작동하면_s3Client_호출됨() {
        String url = CLOUDFRONT_DOMAIN + "/test-dir/test.png";

        s3Uploader.delete(List.of(url));

        verify(s3Client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    void delete_빈목록이면_호출안됨() {
        s3Uploader.delete(List.of());
        verify(s3Client, never())
                .deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    void extractKeyFromUrl_정상작동() {
        String url = CLOUDFRONT_DOMAIN + "/folder/file.png";
        String key = s3Uploader.extractKeyFromUrl(url);
        assertThat(key).isEqualTo("folder/file.png");
    }

    @Test
    void extractKeyFromUrl_도메인불일치시_예외() {
        String url = "https://not-valid.com/folder/file.png";

        assertThatThrownBy(() -> s3Uploader.extractKeyFromUrl(url))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN);
    }

    @Test
    void updateFiles_기존삭제하고_새로업로드() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
        String originalUrl = CLOUDFRONT_DOMAIN + "/some-dir/old-file.png";

        List<String> result = s3Uploader.updateFiles(List.of(originalUrl), List.of(file));

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).startsWith(CLOUDFRONT_DOMAIN + "/some-dir/");
    }
}