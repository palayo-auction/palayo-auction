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
	REFRESH_TOKEN_NOT_FOUND("해당 refresh token을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_REFRESH_TOKEN("유효하지 않은 refresh token 입니다.", HttpStatus.BAD_REQUEST),
	USERID_NOT_MATCH("유저 id가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

	//경매 관련 에러 코드
	INVALID_ITEM_OWNER("아이템은 사용자가 등록한 상품이어야 합니다.", HttpStatus.BAD_REQUEST),
	ITEM_ALREADY_ON_AUCTION("아이템은 이미 경매에 등록된 상품입니다.", HttpStatus.BAD_REQUEST),
	INVALID_START_TIME("시작 시간은 현재 시간 이후여야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_END_TIME("종료 시간은 시작 시간 이후여야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_MINIMUM_PRICE("최소가는 100원 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_STARTING_PRICE("시작가는 즉시 낙찰가보다 작아야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_BID_INCREMENT("입찰 단위는 100, 1,000, 10,000, 100,000, 1,000,000원 중 하나여야 합니다.", HttpStatus.BAD_REQUEST),
	BID_INCREMENT_TOO_HIGH("입찰 단위는 시작가보다 작아야 합니다.", HttpStatus.BAD_REQUEST),
	AUCTION_NOT_FOUND("해당 경매 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	NO_WINNING_BIDDER("경매 종료 시 낙찰자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
	AUCTION_CANNOT_BE_FAILED("경매를 실패 상태로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),

	//경매 이력 관련 에러 코드

	//보증금 이력 관련 에러 코드

	//찜 관련 에러 코드

	//아이템 관련 에러 코드

	//이미지 관련 에러 코드

	//알림 관련 에러 코드

	//포인트 이력 관련 에러 코드

	//그 외 에러 코드
	UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 타입입니다.", HttpStatus.BAD_REQUEST),
	INVALID_TYPE("유효하지 않은 타입입니다.", HttpStatus.BAD_REQUEST),
	SERVER_NOT_WORK("서버 문제로 인해 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String message;
	private final HttpStatus httpStatus;
}
