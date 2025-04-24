package com.example.palayo.domain.websocket.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.domain.auctionhistory.dto.request.CreateBidRequest;
import com.example.palayo.domain.auctionhistory.dto.response.BidResponse;
import com.example.palayo.domain.auctionhistory.service.AuctionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
public class BidWebSocketController {

    private final AuctionHistoryService auctionHistoryService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/bid.send")
    public void handleBid(
            @Payload CreateBidRequest request,
            Message<?> message
    ) {
        log.info("handleBid called");
        log.info("Incoming request.getAuctionId: {} + \nIncoming request.getBidPrice: {}", request.getAuctionId(), request.getBidPrice());

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        AuthUser authUser = (AuthUser) accessor.getSessionAttributes().get("authUser");

        if (authUser == null) {
            log.error("AuthUser is null in the WebSocket session");
            return;
        }

        log.info("Authenticated User: {}", authUser);

        BidResponse response;
        try {
            response = auctionHistoryService.createBid(authUser, request.getAuctionId(), request);
        } catch (BaseException e) {
            log.error("BaseException occurred: {}", e.getMessage(), e);
            messagingTemplate.convertAndSendToUser(
                    authUser.getName(),
                    "/queue/errors",
                    Map.of("errorCode", e.getErrorCode(), "message", e.getMessage())
            );
            return;
        }

        String topic = String.format("/topic/bids.%s", request.getAuctionId());
        messagingTemplate.convertAndSend(topic, response);
    }
}
