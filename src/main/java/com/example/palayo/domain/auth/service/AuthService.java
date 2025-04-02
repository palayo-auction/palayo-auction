package com.example.palayo.domain.auth.service;

import com.example.palayo.domain.auth.dto.response.AuthSaveResponseDto;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthSaveResponseDto authSave(String email, String password, String nickname, int pointAmount) {

        User user = User.of(email, password, nickname, pointAmount);
        User savedUser = userRepository.save(user);

        return AuthSaveResponseDto.of(savedUser);
    }
}
