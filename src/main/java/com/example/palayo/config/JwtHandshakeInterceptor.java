package com.example.palayo.config;

import com.example.palayo.common.dto.AuthUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Log4j2
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("Authorization");

        if (StringUtils.hasText(token)) {
            try {
                String jwt = jwtUtil.substringToken(token);
                Claims claims = jwtUtil.extractClaims(jwt);

                Long userId = Long.valueOf(claims.getSubject());
                String email = claims.get("email", String.class);

                // 프로젝트의 AuthUser 객체 생성
                AuthUser authUser = new AuthUser(userId, email); // 생성자 맞게 수정

                if (email == null) {
                    log.error("JWT token is missing essential claims (userId or email).");
                    return false;  // 토큰에 필수 정보가 없으면 핸드셰이크 거부
                }

                // WebSocket 세션에 사용자 정보 저장
                attributes.put("authUser", authUser);
                log.info("AuthUser from handshake!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: {}", authUser);

            } catch (Exception e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed: {}", exception.getMessage());
        } else {
            log.info("WebSocket handshake successful.");
        }
    }
}
