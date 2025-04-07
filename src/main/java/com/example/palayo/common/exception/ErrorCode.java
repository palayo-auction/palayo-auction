package com.example.palayo.common.exception;

import com.google.api.Http;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //유저 관련 에러 코드
    DUPLICATE_EMAIL("중복된 이메일이 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ROLE("유효하지 않은 역할입니다.", HttpStatus.BAD_REQUEST),
    INVALID_FORM("유효하지 않은 형식입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST("존재하지 않는 회원입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_MISMATCH("존재하지 않는 이메일입니다.", HttpStatus.BAD_REQUEST),
    SIGNIN_FAILED("로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("잘못된 비밀번호입니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PASSWORD_SAME_AS_OLD("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_SAME("새 비밀번호와 새 비밀번호 확인이 다릅니다.", HttpStatus.BAD_REQUEST),
    INACTIVE_USER("이미 탈퇴된 회원입니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND("해당 refresh token을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("유효하지 않은 refresh token 입니다.", HttpStatus.BAD_REQUEST),
    USERID_NOT_MATCH("유저 id가 일치하지 않습니다." , HttpStatus.BAD_REQUEST),

    //경매 관련 에러 코드

    //경매 이력 관련 에러 코드

    //보증금 이력 관련 에러 코드

    //찜 관련 에러 코드

    //아이템 관련 에러 코드

    //이미지 관련 에러 코드

    //알림 관련 에러 코드
    FCM_TOKEN_NOT_FOUND("FCM 토큰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FCM_TOKEN_SAVE_FAILED("FCM 토큰 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FCM_TOKEN_INVALID("유효하지 않은 FCM 토큰입니다.", HttpStatus.BAD_REQUEST),
    FCM_TOKEN_DUPLICATE("이미 등록된 FCM 토큰입니다.", HttpStatus.CONFLICT),
    NOTIFICATION_SEND_FAIL("알림 전송 실패",HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_REGISTERED("알림 등록되지 않았습니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다",HttpStatus.NOT_FOUND),
    //포인트 이력 관련 에러 코드

    //Firebase 관련 에러 코드
    SERVICEACCOUNT_NOT_FOUND("Firebase 서비스 걔정 json파일을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    FIREBASE_INIT_FAIL("Firebase 초기화 실패.",HttpStatus.INTERNAL_SERVER_ERROR),
    //그 외 에러 코드
    UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 타입입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE("유효하지 않은 타입입니다.",HttpStatus.BAD_REQUEST),
    SERVER_NOT_WORK("서버 문제로 인해 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
