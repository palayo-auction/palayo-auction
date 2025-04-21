package com.example.palayo.domain.user.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.user.dto.response.UserResponse;
import com.example.palayo.domain.user.dto.response.UserItemResponse;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public UserResponse updateNickname(String nickname, Long id) {
        User user = findById(id);

        if (nickname.equals(user.getNickname())) {
            throw new BaseException(ErrorCode.NICKNAME_SAME_AS_OLD, nickname);
        }

        userRepository.findByNickname(nickname).ifPresent(
             foundUser -> {
                 throw new BaseException(ErrorCode.DUPLICATE_NICNKNAME, nickname);
             }
        );

        user.updateNickname(nickname);

        return UserResponse.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    @Transactional
    public UserResponse updatePassword(String password, String newPassword, Long userId) {
        User user = findById(userId);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_SAME_AS_OLD, null);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);

        return UserResponse.of(
                userId,
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse myPage(Long userId) {
        User user = findById(userId);

        return UserResponse.of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPointAmount()
        );
    }

     @Transactional(readOnly = true)
     public Page<UserItemResponse> soldItems(Long id, int page, int size) {
         User user = findById(id);

         Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
         Page<Item> items = itemRepository.findBySellerId(user.getId(), pageable);

         return items.map(UserItemResponse::of);
     }

     @Transactional(readOnly = true)
    public Page<UserItemResponse> buyItems(Long userId, int page, int size) {
        User user = findById(userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Auction> auctions = auctionRepository.findByWinningBidder_Id(user.getId(), pageable);
        return auctions.map(auction -> UserItemResponse.of(auction.getItem()));
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
