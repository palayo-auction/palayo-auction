package com.example.palayo.auth.service;

import com.example.palayo.domain.auth.dto.response.SignupUserResponseDto;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.config.JwtUtil;
import com.example.palayo.domain.auth.service.AuthService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private final String email = "test@example.com";

    private final String password = "Password@1";

    private final String nickname = "test";

    private User user;

    @BeforeEach
    void setUp() {
        user = User.of(email, password, nickname);
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    @DisplayName("회원가입, 자동로그인 성공")
    void signupTest() {
        //given
        ReflectionTestUtils.setField(user, "password", "encodedPassword@1");    //테스트 환경에서는 비밀번호가 실제 암호화된 값으로 저장되지 않는 경우가 많음. passwordEncoder.matches 호출시 정상적인 비교를 위해 추가.

        given(userRepository.findByEmail(email))
                .willReturn(Optional.empty())   //회원가입시에는 해당 이메일로 가입된 정보가 없어야한다.
                .willReturn(Optional.of(user)); //로그인시에는 해당 이메일로 가입된 정보가 존재해야한다.

        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());
        given(passwordEncoder.encode(password)).willReturn("encodedPassword@1");
        given(passwordEncoder.matches(password, "encodedPassword@1")).willReturn(true);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtUtil.createToken(user.getId(), user.getEmail())).willReturn("testToken");

        //when
        SignupUserResponseDto responseDto = authService.signUp(email, password, nickname);

        //then
        assertEquals(email, responseDto.getEmail());
        assertEquals(nickname, responseDto.getNickname());
        assertEquals("testToken", responseDto.getBearerToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입시 이메일 중복 실패")
    void signupDuplicatedEmailTest() {
        //given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());

        //when
        BaseException exception = assertThrows(BaseException.class, () -> authService.signUp(email, password, nickname));

        //then
        assertEquals(ErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
        assertEquals("중복된 이메일이 있습니다.", exception.getMessage());
        assertEquals(email, exception.getField());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    @DisplayName("회원가입시 닉네임 중복 실패")
    void signupDuplicatedNicknameTest() {
        //given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));

        //when
        BaseException exception = assertThrows(BaseException.class, () -> authService.signUp(email, password, nickname));

        //then
        assertEquals(ErrorCode.DUPLICATE_NICNKNAME, exception.getErrorCode());
        assertEquals("중복된 닉네임이 있습니다.", exception.getMessage());
        assertEquals(nickname, exception.getField());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    @DisplayName("로그인시 이메일이 존재하지 않음")
    void loginEmailWrongTest() {
        //given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        BaseException exception = assertThrows(BaseException.class, () -> authService.login(email, password));

        //then
        assertEquals(ErrorCode.EMAIL_MISMATCH, exception.getErrorCode());
        assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
        assertEquals(email, exception.getField());
        verify(jwtUtil, times(0)).createToken(user.getId(), email);
    }

    @Test
    @DisplayName("로그인시 비밀번호를 틀림")
    void loginPasswordWrongTest() {
        //given
        ReflectionTestUtils.setField(user, "password", "encodedPassword@1");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword@1")).willReturn(false);

        //when
        BaseException exception = assertThrows(BaseException.class, () -> authService.login(email, "wrongPassword"));

        //then
        assertEquals(ErrorCode.PASSWORD_MISMATCH, exception.getErrorCode());
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        assertNull(null, exception.getField());
        verify(jwtUtil, times(0)).createToken(user.getId(), email);
    }
}
