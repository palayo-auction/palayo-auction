package com.example.palayo.domain.user.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.user.dto.response.UserResponseDto;
import com.example.palayo.domain.user.dto.response.UserItemResponseDto;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ItemRepository itemRepository;

    @Transactional
    public UserResponseDto updateNickname(String nickname, Long id) {
        User me = isMe(id);

        if (nickname.equals(me.getNickname())) {
            throw new BaseException(ErrorCode.NICKNAME_SAME_AS_OLD, nickname);
        }

        me.updateNickname(nickname);

        return UserResponseDto.of(
                me.getId(),
                me.getEmail(),
                me.getNickname(),
                me.getPointAmount()
        );
    }

    @Transactional
    public UserResponseDto updatePassword(String password, String newPassword, Long userId) {
        User me = isMe(userId);

        if (passwordEncoder.matches(newPassword, me.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_SAME_AS_OLD, null);
        }

        if (passwordEncoder.matches(password, me.getPassword())) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            me.updatePassword(encodedPassword);
        } else throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);

        return UserResponseDto.of(
                userId,
                me.getEmail(),
                me.getNickname(),
                me.getPointAmount()
        );
    }

    public UserResponseDto mypage(Long userId) {
        User me = isMe(userId);

        return UserResponseDto.of(
                me.getId(),
                me.getEmail(),
                me.getNickname(),
                me.getPointAmount()
        );
    }

    @Transactional(readOnly = true)
    public Page<UserItemResponseDto> sold(Long id, int page, int size) {
        User me = isMe(id);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Item> items = itemRepository.findByUserId(me.getId(), pageable);

        return items.map(UserItemResponseDto::of);
    }

    public void delete(Long userId, String password) {
        User me = isMe(userId);
        
        if (passwordEncoder.matches(password, me.getPassword())) {
            userRepository.delete(me);
        } else throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
    }


    //계속 중복되어 메서드화
    private User isMe(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );
    }


}
