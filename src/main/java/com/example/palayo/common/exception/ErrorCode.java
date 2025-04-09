package com.example.palayo.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //유저 관련 에러 코드
    DUPLICATE_EMAIL("중복된 이메일이 있습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_MISMATCH("존재하지 않는 이메일입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("잘못된 비밀번호입니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PASSWORD_SAME_AS_OLD("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INACTIVE_USER("이미 탈퇴된 회원입니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_SAME_AS_OLD("새 닉네임이 기존의 닉네임과 같을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICNKNAME("중복된 닉네임이 있습니다.", HttpStatus.BAD_REQUEST),
    //결제 관련 에러 코드
    PAYMENT_TIMEOUT("결제 시간이 오래걸려 취소됐습니다.", HttpStatus.REQUEST_TIMEOUT),
    INVALID_PAYMENT_AMOUNT("결제 대금은 천원 이상이여야 합니다.", HttpStatus.BAD_REQUEST),
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
	UNAUTHORIZED_ACCESS("해당 경매에 접근할 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
	INVALID_AUCTION_STATUS("허용되지 않은 경매 상태입니다.", HttpStatus.BAD_REQUEST),
	ALREADY_DELETED_AUCTION("이미 삭제된 경매입니다.", HttpStatus.BAD_REQUEST),
	CANNOT_DELETE_ACTIVE_AUCTION("진행 중인 경매는 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
	//경매 이력 관련 에러 코드

    //보증금 이력 관련 에러 코드

    //찜 관련 에러 코드

    //아이템 관련 에러 코드
    CATEGORY_NOT_FOUND("해당 카테고리는 존재하지 않습니다.",HttpStatus.NOT_FOUND),
    STATUS_NOT_FOUND("해당 상태는 존재하지 않습니다.",HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND("해당 하는 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ITEM_NAME("이름이 중복된 상품이 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ITEM_STATUS_FOR_UPDATE("경매 이전의 상품만 수정할 수 있습니다.", HttpStatus.BAD_REQUEST),
    ITEM_EDIT_FORBIDDEN("상품 상태를 수정할 권한이 없습니다.", HttpStatus.FORBIDDEN),
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
