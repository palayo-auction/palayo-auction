package com.example.palayo.domain.user.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.user.dto.response.UpdateUserResponseDto;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UpdateUserResponseDto updateNickname(String nickname, Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );

        if (nickname.equals(user.getNickname())) {
            throw new BaseException(ErrorCode.NICKNAME_SAME_AS_OLD, nickname);
        }

        user.updateNickname(nickname);

        return UpdateUserResponseDto.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    @Transactional
    public UpdateUserResponseDto updatePassword(String password, String newPassword, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_SAME_AS_OLD, null);
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.updatePassword(encodedPassword);
        } else throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);

        return UpdateUserResponseDto.of(
                userId,
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }
}
