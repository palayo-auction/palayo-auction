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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SignupUserResponseDto singup(String email, String password, String nickname) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        Optional<User> byNickname = userRepository.findByNickname(nickname);

        if (byEmail.isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_EMAIL, email);
        }

        if (byNickname.isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_NICNKNAME, nickname);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.of(email, encodedPassword, nickname);
        User savedUser = userRepository.save(user);

        LoginUserResponseDto login = login(email, password);
        String bearerToken = login.getToken();
//        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail());

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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        return LoginUserResponseDto.of(
                bearerToken);
    }
}
