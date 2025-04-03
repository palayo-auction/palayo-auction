package com.example.palayo.auth.service;

import com.example.palayo.auth.dto.response.LoginUserResponseDto;
import com.example.palayo.auth.dto.response.SignupUserResponseDto;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.config.JwtUtil;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SignupUserResponseDto singup(String email, String password, String nickname) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.of(email, encodedPassword, nickname);
        User savedUser = userRepository.save(user);
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail());

        return SignupUserResponseDto.of(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getPointAmount(),
                bearerToken
        );
    }

    public LoginUserResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BaseException(ErrorCode.USERID_NOT_MATCH, null)
        );
        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail());
        boolean matches = passwordEncoder.matches(password, user.getPassword());

        if (!matches) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        return LoginUserResponseDto.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount(),
                bearerToken);
    }
}
