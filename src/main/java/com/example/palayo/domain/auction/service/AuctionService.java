package com.example.palayo.domain.auction.service;

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
import com.example.palayo.domain.auction.util.AuctionValidator;
import com.example.palayo.domain.auction.util.TimeFormatter;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final ItemRepository itemRepository;
	private final AuctionServiceHelper auctionServiceHelper;
	private final AuctionValidator auctionValidator;

	// 경매 생성 (상품 ID 검증 후 경매 등록)
	@Transactional
	public AuctionResponse saveAuction(AuthUser authUser, CreateAuctionRequest request) {

		// 상품 존재/소유자/중복 경매 검증
		Item item = auctionValidator.validateAuctionCreation(request, authUser);

		// 경매 엔티티 생성 및 초기값 설정
		Auction auction = Auction.of(
			item,
			request.getStartingPrice(),
			request.getBuyoutPrice(),
			request.getBidIncrement(),
			request.getStartedAt(),
			request.getExpiredAt()
		);
		auction.markAsReady(); // 상태 READY 설정
		auction.updateCurrentPrice(request.getStartingPrice()); // 시작가로 현재 최고 입찰가 초기화

		// 경매 저장 후 응답 반환
		Auction savedAuction = auctionRepository.save(auction);

		return AuctionResponse.of(savedAuction);
	}

	// 경매 상태 업데이트 (시간 기준 READY, ACTIVE, SUCCESS, FAILED 변경)
	@Transactional
	public boolean updateAuctionStatus(Auction auction) {
		return auctionServiceHelper.updateStatus(auction);
	}

	// 낙찰자 선정 (종료시간 지났거나 즉시낙찰가 도달 시 낙찰자 설정)
	@Transactional
	public boolean assignWinningBidder(Auction auction) {
		return auctionServiceHelper.assignWinningBidder(auction);
	}

	// 경매 다건 조회 (READY, ACTIVE)
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getAuctions(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<Auction> auctions = auctionRepository.findAllByStatusIn(
			List.of(AuctionStatus.READY, AuctionStatus.ACTIVE),
			pageable
		);

		LocalDateTime now = LocalDateTime.now();
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction))
		);
	}

	// 경매 단건 조회 (READY, ACTIVE 상태만 조회 가능)
	@Transactional(readOnly = true)
	public AuctionDetailResponse getAuction(Long auctionId) {
		Auction auction = findAuctionByIdAndStatus(auctionId, List.of(AuctionStatus.READY, AuctionStatus.ACTIVE));

		LocalDateTime now = LocalDateTime.now();
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), null);
	}

	// 내가 등록한 경매 목록 조회 (본인이 등록한 경매 전체 조회, 삭제된 경매 포함)
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getMyAuctions(AuthUser authUser, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

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
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction))
		);
	}

	// 내가 등록한 경매 단건 조회 (본인이 등록한 경매 상세 조회 + 낙찰자 닉네임 추가 반환)
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

		// 소유자 검증
		auctionServiceHelper.validateOwnership(authUser, auction);

		// 낙찰자 닉네임 세팅 (성공 or 삭제되었지만 과거 성공했던 경우)
		String winningBidderNickname = null;
		if (auction.getStatus() == AuctionStatus.SUCCESS ||
			(auction.getStatus() == AuctionStatus.DELETED && auction.getWinningBidder() != null)) {
			winningBidderNickname = auction.getWinningBidder().getNickname();
		}

		LocalDateTime now = LocalDateTime.now();
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction),
			winningBidderNickname);
	}

	// 경매 삭제 (소프트 딜리트)
	@Transactional
	public void deleteAuction(AuthUser authUser, Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		// 소유자 검증
		auctionServiceHelper.validateOwnership(authUser, auction);

		// 삭제 가능 상태 검증
		auctionServiceHelper.validateDeletableAuction(auction);

		// 소프트 딜리트 처리
		auction.markAsDeleted();
	}

	// 경매 ID + 상태로 경매 조회 (없으면 예외 던짐)
	// 검증이나 비즈니스 로직이 아닌 단순 조회용 메서드라 AuctionService 내부에 둠
	private Auction findAuctionByIdAndStatus(Long auctionId, List<AuctionStatus> statuses) {
		return auctionRepository.findByIdAndStatusIn(auctionId, statuses)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));
	}
}

