package com.example.palayo.common.dto;

import lombok.Getter;

@Getter
public class AuthUser {

    private final Long userId;
    private final String email;
//    private final UserRole userRole;
//    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long userId, String email/*, UserRole role*/) {
        this.userId = userId;
        this.email = email;
//        this.userRole = role;
//        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }
}