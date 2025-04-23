package com.example.palayo.config;

import com.example.palayo.common.dto.AuthUser;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class AuthUserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return (AuthUser) attributes.get("authUser"); // AuthUser는 Principal 구현체여야 함!
    }
}
