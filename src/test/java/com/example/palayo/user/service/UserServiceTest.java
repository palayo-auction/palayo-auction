package com.example.palayo.user.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.user.dto.response.UserResponse;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import com.example.palayo.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private User user;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNickNameTest() {
        String newNickname = "newNickname";

        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(user.getNickname())
                .willReturn("oldNickname")  //처음에는 변경 전 닉네임이 나와야함
                .willReturn(newNickname); //변경후에 getNickname() 호출시 변경 된 닉네임이 나와야함.
        given(userRepository.findByNickname(newNickname)).willReturn(Optional.of(user));

        //when
        UserResponse response = userService.updateNickname(newNickname, user.getId());

        //then
        verify(user).updateNickname(newNickname);
        assertEquals(newNickname, response.getNickname());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePasswordTest() {
        String oldPassword = "oldPassword@1";
        String newPassword = "newPassword@1";
        String encodedPassword = "encodedPassword@1";

        //given
        ReflectionTestUtils.setField(user, "password", encodedPassword);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, encodedPassword)).willReturn(false);
        given(passwordEncoder.matches(oldPassword, encodedPassword)).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);
        given(user.getPassword()).willReturn(encodedPassword);

        //when
        UserResponse response = userService.updatePassword(oldPassword, newPassword, user.getId());

        //then
        assertEquals(user.getId(), response.getId());
        verify(user).updatePassword(encodedPassword);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteTest() {
        //given
        ReflectionTestUtils.setField(user, "password", "test");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("test", user.getPassword())).willReturn(true);

        //when
        userService.delete(user.getId(), "test");

        //then
        verify(user, times(1)).deleteUser();
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("닉네임 변경시 새 닉네임이 기존 닉네임과 같아 실패")
    void duplicatedNewNicknameTest() {
        String nickname = "nickname";

        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(user.getNickname()).willReturn(nickname);

        //when
        BaseException exception = assertThrows(BaseException.class, () -> userService.updateNickname(nickname, user.getId()));

        //then
        assertEquals(ErrorCode.NICKNAME_SAME_AS_OLD, exception.getErrorCode());
        assertEquals("새 닉네임이 기존의 닉네임과 같을 수 없습니다.", exception.getMessage());
        assertEquals(user.getNickname(), exception.getField());
        verify(user, times(0)).updateNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 변경시 새 닉네임이 다른 유저의 닉네임과 같아 실패")
    void duplicatedAnotherUserNickname() {
        String nickname = "nickname";
        String newNickname = "newNickname";

        //given
        User oldUser = User.of("test", "test", nickname);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findByNickname(newNickname)).willReturn(Optional.of(oldUser));

        //when
        BaseException exception = assertThrows(BaseException.class, () -> userService.updateNickname(newNickname, user.getId()));

        //then
        assertEquals(ErrorCode.DUPLICATE_NICNKNAME, exception.getErrorCode());
        assertEquals("중복된 닉네임이 있습니다.", exception.getMessage());
        assertEquals(newNickname, exception.getField());
        verify(user, times(0)).updateNickname(newNickname);
    }

    @Test
    @DisplayName("비밀번호 변경시 변경할 비밀번호가 기존의 비밀번호와 같아 실패")
    void duplicatedPasswordTest() {
        String newPassword = "newPassword@1";
        String encodedPassword = "encodedPassword@1";

        //given
        ReflectionTestUtils.setField(user, "password", encodedPassword);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, encodedPassword)).willReturn(true);
        given(user.getPassword()).willReturn(encodedPassword);

        //when
        BaseException exception = assertThrows(BaseException.class, () -> userService.updatePassword(newPassword, newPassword, user.getId()));

        //then
        assertEquals(ErrorCode.PASSWORD_SAME_AS_OLD, exception.getErrorCode());
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
        assertNull(null, exception.getField());
        verify(user, times(0)).updatePassword(newPassword);
    }

    @Test
    @DisplayName("비밀번호 변경시 비밀번호가 틀려서 실패")
    void passwordMismatchTest() {
        String oldPassword = "oldPassword@1";
        String newPassword = "newPassword@1";
        String encodedPassword = "encodedPassword@1";

        //given
        ReflectionTestUtils.setField(user, "password", encodedPassword);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, encodedPassword)).willReturn(false);
        given(passwordEncoder.matches(oldPassword, encodedPassword)).willReturn(false);
        given(user.getPassword()).willReturn(encodedPassword);

        //when
        BaseException exception = assertThrows(BaseException.class, () -> userService.updatePassword(oldPassword, newPassword, user.getId()));

        //then
        assertEquals(ErrorCode.PASSWORD_MISMATCH, exception.getErrorCode());
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        assertNull(null, exception.getField());
        verify(user, times(0)).updatePassword(newPassword);
    }

    @Test
    @DisplayName("유저 삭제시 비밀번호가 틀려서 실패")
    void deletePasswordMismatchTest() {
        String oldPassword = "oldPassword@1";
        String encodedPassword = "encodedPassword@1";

        //given
        ReflectionTestUtils.setField(user, "password", encodedPassword);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, encodedPassword)).willReturn(false);
        given(user.getPassword()).willReturn(encodedPassword);

        //when
        BaseException exception = assertThrows(BaseException.class, () -> userService.delete(user.getId(), oldPassword));

        //then
        assertEquals(ErrorCode.PASSWORD_MISMATCH, exception.getErrorCode());
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        assertNull(null, exception.getField());
        verify(user, times(0)).deleteUser();
    }
}
