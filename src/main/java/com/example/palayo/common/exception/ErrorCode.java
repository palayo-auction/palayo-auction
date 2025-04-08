package com.example.palayo.common.exception;

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
    USER_EMAIL_NOT_MATCH("유저 Email이 일치하지 않습니다." , HttpStatus.BAD_REQUEST),
    NICKNAME_SAME_AS_OLD("새 닉네임이 기존의 닉네임과 같을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICNKNAME("중복된 닉네임이 있습니다.", HttpStatus.BAD_REQUEST),
    //경매 관련 에러 코드

    //경매 이력 관련 에러 코드

    //보증금 이력 관련 에러 코드

    //찜 관련 에러 코드

    //아이템 관련 에러 코드

    //이미지 관련 에러 코드

    //알림 관련 에러 코드

    //포인트 이력 관련 에러 코드

    //그 외 에러 코드
    UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 타입입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE("유효하지 않은 타입입니다.",HttpStatus.BAD_REQUEST),
//    DUPLICATE_UNIQUE("기존 데이터베이스 정보와 중복됩니다.", HttpStatus.BAD_REQUEST),
    SERVER_NOT_WORK("서버 문제로 인해 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
