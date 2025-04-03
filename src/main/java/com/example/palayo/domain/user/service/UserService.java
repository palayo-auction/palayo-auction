package com.example.palayo.domain.user.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.user.dto.response.UserResponseDto;
import com.example.palayo.domain.user.dto.response.UserSoldResponseDto;
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
    public UserResponseDto updateNickname(String nickname, Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );

        if (nickname.equals(user.getNickname())) {
            throw new BaseException(ErrorCode.NICKNAME_SAME_AS_OLD, nickname);
        }

        user.updateNickname(nickname);

        return UserResponseDto.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    @Transactional
    public UserResponseDto updatePassword(String password, String newPassword, Long userId) {
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

        return UserResponseDto.of(
                userId,
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    public UserResponseDto mypage(Long id, Long userId) {
        User me = isMe(id, userId);

        return UserResponseDto.of(
                me.getId(),
                me.getEmail(),
                me.getNickname(),
                me.getPointAmount()
        );
    }

    public UserSoldResponseDto sold(Long id, Long userId) {
        User me = isMe(id, userId);


    }

    
    //마이페이지 조회때 내가 맞는지 검증하는 메서드
    private User isMe(Long pathId, Long userId) {
        User me = userRepository.findById(pathId).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );

        if (!me.getId().equals(userId)) {
            throw new BaseException(ErrorCode.USERID_NOT_MATCH, null);
        }

        return me;
    }
}
