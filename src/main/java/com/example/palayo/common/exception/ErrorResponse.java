package com.example.palayo.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private List<ErrorDetail> errorDetails;
    private LocalDateTime timestamp;

    public static ErrorResponse of(List<ErrorDetail> errorDetails) {
        return new ErrorResponse(errorDetails, LocalDateTime.now());
    }

    public static ErrorResponse of(ErrorDetail errorDetail) {
        return new ErrorResponse(List.of(errorDetail), LocalDateTime.now());
    }
}
