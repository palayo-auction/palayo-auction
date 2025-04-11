package com.example.palayo.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorDetail errorDetail = new ErrorDetail(
                ex.getField(),
                ex.getErrorCode().getMessage(),
                ex.getErrorCode().name()
        );
        log.error("[에러발생]",ex);
        return new ResponseEntity<>(ErrorResponse.of(errorDetail), ex.getErrorCode().getHttpStatus());
    }

    // Valid 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetail(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getCode() // NotBlank, Size 등
                ))
                .collect(Collectors.toList());
        return new ResponseEntity<>(ErrorResponse.of(errorDetails), HttpStatus.BAD_REQUEST);
    }

     // 기타 예외 처리
     @ExceptionHandler(Exception.class)
     public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
         ErrorDetail errorDetail = new ErrorDetail(
                 null,
                 ex.getMessage(),
                 ErrorCode.SERVER_NOT_WORK.name()
         );
         log.error("[에러발생]",ex);
         return new ResponseEntity<>(ErrorResponse.of(errorDetail), HttpStatus.INTERNAL_SERVER_ERROR);
     }

    // TossPayments 등 외부 API 호출 실패 처리
    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity<ErrorResponse> handleHttpClientException(RestClientException ex) {
        ErrorDetail errorDetail = new ErrorDetail(
                null,
                "외부 결제 서버 오류: " + ex.getMessage(),
                ErrorCode.EXTERNAL_API_ERROR.name()
        );
        log.error("[외부 API 호출 에러]", ex);
        return new ResponseEntity<>(ErrorResponse.of(errorDetail), HttpStatus.BAD_GATEWAY);
    }

    // Entity 클래스의 unique 제약조건 필드 중복 오류처리
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
//        ErrorDetail errorDetail = new ErrorDetail(
//                null,
//                ex.getMessage(),
//                ErrorCode.DUPLICATE_UNIQUE.name()
//        );
//        log.error("[에러발생]", ex);
//        return new ResponseEntity<>(ErrorResponse.of(errorDetail), HttpStatus.BAD_REQUEST);
//    }
}