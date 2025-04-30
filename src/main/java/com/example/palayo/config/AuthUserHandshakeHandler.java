package com.example.palayo.config;

import com.example.palayo.common.dto.AuthUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Log4j2
@Component
public class AuthUserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(attributes.get("authUser") instanceof AuthUser)) {
            log.warn("No AuthUser found in WebSocket session attributes.");
            return null;
        }
        return (AuthUser) attributes.get("authUser"); // AuthUser는 Principal 구현체여야 함!
    }
}
