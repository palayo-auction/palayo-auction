package com.example.palayo.domain.auction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
import com.example.palayo.domain.auction.dto.response.AuctionResponse;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.auction.util.TimeFormatter;
import com.example.palayo.domain.auction.util.AuctionValidator;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final ItemRepository itemRepository;
	private final AuctionServiceHelper auctionServiceHelper;
	private final AuctionValidator auctionValidator;

	// 사용자가 경매를 생성할 때 호출하는 메서드
	// 상품이 존재하는지, 주인인지, 이미 경매중인지 확인한 후 경매를 새로 만든다
	@Transactional
	public AuctionResponse saveAuction(AuthUser authUser, CreateAuctionRequest request) {

		// 상품 존재, 소유자 일치 여부, 중복 경매 여부 검증
		Item item = auctionValidator.validateAuctionCreation(request, authUser);

		// 검증 통과 후 경매 객체 생성 + 초기값 설정 (상태는 READY, 현재 입찰가는 시작가로)
		Auction auction = Auction.of(
			item,
			request.getStartingPrice(),
			request.getBuyoutPrice(),
			request.getBidIncrement(),
			request.getStartedAt(),
			request.getExpiredAt()
		);
		auction.markAsReady(); // 경매 상태를 READY로 설정
		auction.updateCurrentPrice(request.getStartingPrice()); // 현재 입찰가를 시작가로 설정

		// DB에 경매 저장 후 저장된 경매를 응답으로 변환하여 반환
		Auction savedAuction = auctionRepository.save(auction);
		return AuctionResponse.of(savedAuction);
	}

	// 시간에 따라 경매 상태(READY -> ACTIVE -> SUCCESS/FAILED)를 갱신하는 메서드
	@Transactional
	public boolean updateAuctionStatus(Auction auction) {
		return auctionServiceHelper.updateStatus(auction);
	}

	// 경매 종료 시 최고 입찰자를 낙찰자로 지정하는 메서드
	@Transactional
	public boolean assignWinningBidder(Auction auction) {
		return auctionServiceHelper.assignWinningBidder(auction);
	}

	// 현재 진행중인 경매(READY, ACTIVE 상태)를 페이지 단위로 조회하는 메서드
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getAuctions(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// READY나 ACTIVE 상태인 경매만 조회
		Page<Auction> auctions = auctionRepository.findAllByStatusIn(
			List.of(AuctionStatus.READY, AuctionStatus.ACTIVE),
			pageable
		);

		LocalDateTime now = LocalDateTime.now();
		// 각 경매에 대해 남은 시간 포맷팅해서 응답 변환
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction))
		);
	}

	// 특정 경매 하나를 조회하는 메서드 (READY, ACTIVE 상태만 조회 가능)
	@Transactional(readOnly = true)
	public AuctionDetailResponse getAuction(Long auctionId) {
		Auction auction = findAuctionByIdAndStatus(auctionId, List.of(AuctionStatus.READY, AuctionStatus.ACTIVE));

		LocalDateTime now = LocalDateTime.now();
		// 낙찰자 정보 없이 경매 상세 응답 반환
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), null);
	}

	// 내가 등록한 모든 경매를 조회하는 메서드 (삭제된 경매 포함)
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getMyAuctions(AuthUser authUser, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// 나의 아이디로 등록된 모든 상태의 경매 조회
		Page<Auction> auctions = auctionRepository.findAllByItemSellerIdAndStatusIn(
			authUser.getUserId(),
			List.of(
				AuctionStatus.READY,
				AuctionStatus.ACTIVE,
				AuctionStatus.SUCCESS,
				AuctionStatus.FAILED,
				AuctionStatus.DELETED
			),
			pageable
		);

		LocalDateTime now = LocalDateTime.now();
		// 각 경매의 남은 시간을 함께 응답
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction))
		);
	}

	// 내가 등록한 특정 경매 하나를 상세 조회하는 메서드 (낙찰자 닉네임도 함께 반환)
	@Transactional(readOnly = true)
	public AuctionDetailResponse getMyAuction(AuthUser authUser, Long auctionId) {
		Auction auction = findAuctionByIdAndStatus(
			auctionId,
			List.of(
				AuctionStatus.READY,
				AuctionStatus.ACTIVE,
				AuctionStatus.SUCCESS,
				AuctionStatus.FAILED,
				AuctionStatus.DELETED
			)
		);

		// 요청자가 진짜 이 경매의 주인인지 확인
		auctionServiceHelper.validateOwnership(authUser, auction);

		// 낙찰자가 존재하는 경우 닉네임 세팅 (SUCCESS이거나, 과거에 SUCCESS였다가 DELETED된 경우)
		String winningBidderNickname = null;
		if (auction.getStatus() == AuctionStatus.SUCCESS ||
			(auction.getStatus() == AuctionStatus.DELETED && auction.getWinningBidder() != null)) {
			winningBidderNickname = auction.getWinningBidder().getNickname();
		}

		LocalDateTime now = LocalDateTime.now();
		// 낙찰자 정보까지 담아서 경매 상세 응답 반환
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction),
			winningBidderNickname);
	}

	// 경매를 삭제하는 메서드 (소프트 딜리트: 실제 삭제가 아니라 상태만 변경)
	@Transactional
	public void deleteAuction(AuthUser authUser, Long auctionId) {
		// 경매 ID로 경매 조회 (없으면 예외 발생)
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		// 요청자가 경매 주인인지 확인
		auctionServiceHelper.validateOwnership(authUser, auction);

		// 현재 상태로 삭제가 가능한지 확인 (ACTIVE 상태는 삭제 불가)
		auctionServiceHelper.validateDeletableAuction(auction);

		// 경매 상태를 DELETED로 변경
		auction.markAsDeleted();
	}

	// 특정 경매를 ID와 상태 리스트를 기준으로 조회하는 메서드
	// 내부에서 검증이나 비즈니스 로직 전에 조회용으로 사용
	private Auction findAuctionByIdAndStatus(Long auctionId, List<AuctionStatus> statuses) {
		return auctionRepository.findByIdAndStatusIn(auctionId, statuses)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));
	}
}