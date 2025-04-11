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
    EXTERNAL_API_ERROR("토스 페이먼츠 API 호출 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
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
	BID_PRICE_TOO_LOW("입찰 금액은 현재 가격 + 입찰 단위 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
	CANNOT_BID_OWN_AUCTION("자신이 등록한 경매에는 입찰할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_POINT("보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    //보증금 이력 관련 에러 코드
    DEPOSIT_HISTORY_NOT_FOUND("보증금 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_DEPOSIT_AMOUNT("잘못된 보증금 금액입니다.", HttpStatus.BAD_REQUEST),
    INVALID_STATUS("잘못된 보증금 상태입니다.", HttpStatus.BAD_REQUEST),
    DEPOSIT_HISTORY_ALREADY_EXISTS("이미 존재하는 보증금 이력입니다.", HttpStatus.BAD_REQUEST),
	//찜 관련 에러 코드
	DIB_NOT_FOUND("찜한 경매가 존재하지 않습니다.",HttpStatus.NOT_FOUND),
	DIB_FORBIDDEN("찜한 경매를 조회할 권한이 없습니다.",HttpStatus.UNAUTHORIZED),
	//아이템 관련 에러 코드
	CATEGORY_NOT_FOUND("해당 카테고리는 존재하지 않습니다.",HttpStatus.NOT_FOUND),
	STATUS_NOT_FOUND("해당 상태는 존재하지 않습니다.",HttpStatus.NOT_FOUND),
	ITEM_NOT_FOUND("해당 하는 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_ITEM_NAME("이름이 중복된 상품이 있습니다.", HttpStatus.BAD_REQUEST),
	INVALID_ITEM_STATUS_FOR_UPDATE("경매 이전의 상품만 수정할 수 있습니다.", HttpStatus.BAD_REQUEST),
	ITEM_EDIT_FORBIDDEN("상품 상태를 수정할 권한이 없습니다.", HttpStatus.FORBIDDEN),
	//이미지 관련 에러 코드
    IMAGE_NOT_FOUND("해당 이미지는 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_IMAGE("이미 등록된 있습니다.", HttpStatus.BAD_REQUEST),
    //알림 관련 에러 코드
    FCM_TOKEN_NOT_FOUND("FCM 토큰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FCM_TOKEN_SAVE_FAILED("FCM 토큰 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FCM_TOKEN_INVALID("유효하지 않은 FCM 토큰입니다.", HttpStatus.BAD_REQUEST),
    FCM_TOKEN_DUPLICATE("이미 등록된 FCM 토큰입니다.", HttpStatus.CONFLICT),
    NOTIFICATION_SEND_FAIL("알림 전송 실패",HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_REGISTERED("알림 등록되지 않았습니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다",HttpStatus.NOT_FOUND),
    //JsonConverter 관련 에러 코드
    JSON_CONVERT_FAIL("JSON 변환 실패",HttpStatus.BAD_REQUEST),
    JSON_PARSING_FAIL("JSON 파싱 실패",HttpStatus.BAD_REQUEST),
    //포인트 이력 관련 에러 코드

    //Firebase 관련 에러 코드
    SERVICEACCOUNT_NOT_FOUND("Firebase 서비스 걔정 json파일을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    FIREBASE_INIT_FAIL("Firebase 초기화 실패.",HttpStatus.INTERNAL_SERVER_ERROR),

    //그 외 에러 코드
    TOO_MANY_FILES("최대 10개까지 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
    INVALID_DOMAIN("유효한 도메인이 아닙니다.", HttpStatus.BAD_REQUEST),
    EXTERNAL_SERVER_ERROR("외부 API 서버에서 알 수 없는 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    FILE_TOO_LARGE("파일 용량이 초과하였습니다.", HttpStatus.BAD_REQUEST),
    TOTAL_SIZE_EXCEEDED("전체 파일 용량이 100MB를 초과했습니다.", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_FILE_EXTENSION("지원하지 않는 파일 확장자입니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE("유효하지 않은 타입입니다.",HttpStatus.BAD_REQUEST),
//    DUPLICATE_UNIQUE("기존 데이터베이스 정보와 중복됩니다.", HttpStatus.BAD_REQUEST),
    SERVER_NOT_WORK("서버 문제로 인해 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String message;
	private final HttpStatus httpStatus;
}
