package com.example.palayo.domain.auth.service;

import com.example.palayo.domain.auth.dto.response.LoginUserResponse;
import com.example.palayo.domain.auth.dto.response.SignupUserResponse;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.config.JwtUtil;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedissonClient redissonClient; // Redis 사용

    @Transactional
    public SignupUserResponse singUp(String email, String password, String nickname) {
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

        // // 회원가입 완료 후 Redis 포인트 초기화
        // initializeUserPointInRedis(savedUser.getId());

        LoginUserResponse login = login(email, password);
        String bearerToken = login.getToken();

        return SignupUserResponse.of(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getPointAmount(),
                bearerToken
        );
    }

    @Transactional
    public LoginUserResponse login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BaseException(ErrorCode.EMAIL_MISMATCH, email)
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        // // 로그인 성공 후 Redis 포인트 초기화
        // initializeUserPointInRedis(user.getId());

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getNickname());

        return LoginUserResponse.of(
                bearerToken);
    }

    // private void initializeUserPointInRedis(Long userId) {
    //     String redisKey = "user:point:" + userId;
    //     RAtomicLong userPoint = redissonClient.getAtomicLong(redisKey);
    //
    //     if (!userPoint.isExists()) {
    //         userPoint.set(0L); // 처음 생성될 때 0포인트로 초기화
    //     }
    // }
}
