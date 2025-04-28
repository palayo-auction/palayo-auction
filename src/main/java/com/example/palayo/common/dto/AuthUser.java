package com.example.palayo.common.dto;

import lombok.Getter;

import java.io.Serializable;
import java.security.Principal;

@Getter
public class AuthUser implements Principal, Serializable {

    private final Long userId;
    private final String email;
    private final String nickname;
//    private final UserRole userRole;
//    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long userId, String email, String nickname/*, UserRole role*/) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
//        this.userRole = role;
//        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getName() {
        return String.valueOf(nickname);
    }
}