package com.example.palayo.config;

import com.example.palayo.common.dto.AuthUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
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
        if (headers.getOrigin() != null && !(headers.getOrigin().equals("http://localhost:63342") || !headers.getOrigin().equals("https://localhost:8080"))) {
            response.setStatusCode(HttpStatus.FORBIDDEN);  // CORS 정책 위반 시
            return false;
        }

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");

            if (StringUtils.hasText(token)) {
                try {
                    String jwt = jwtUtil.substringToken(token); // "Bearer " 떼기
                    Claims claims = jwtUtil.extractClaims(jwt);

                    Long userId = Long.valueOf(claims.getSubject());
                    String email = claims.get("email", String.class);

                    if (email == null) {
                        log.error("JWT token is missing essential claims.");
                        return false;
                    }

                    AuthUser authUser = new AuthUser(userId, email);
                    attributes.put("authUser", authUser);
                    log.info("AuthUser from handshake: {}", authUser);

                } catch (Exception e) {
                    log.error("Invalid JWT token: {}", e.getMessage());
                    return false;
                }
            } else {
                log.warn("No token found in query parameter.");
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
