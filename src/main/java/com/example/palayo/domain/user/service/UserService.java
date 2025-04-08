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
        User user = findById(id);

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
        User user = findById(userId);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_SAME_AS_OLD, null);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);

        return UserResponseDto.of(
                userId,
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    public UserResponseDto mypage(Long userId) {
        User user = findById(userId);

        return UserResponseDto.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    @Transactional(readOnly = true)
    public Page<UserItemResponseDto> sold(Long id, int page, int size) {
        User user = findById(id);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Item> items = itemRepository.findBySellerId(user.getId(), pageable);

        return items.map(UserItemResponseDto::of);
    }

    @Transactional
    public void delete(Long userId, String password) {
        User user = findById(userId);
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        user.deleteUser();
    }


    //계속 중복되어 메서드화
    private User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );
    }
}
