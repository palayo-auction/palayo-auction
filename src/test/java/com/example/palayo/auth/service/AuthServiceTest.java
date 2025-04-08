package com.example.palayo.auth.service;

import com.example.palayo.auth.dto.response.SignupUserResponseDto;
import com.example.palayo.config.JwtUtil;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("회원가입 성공")
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
        SignupUserResponseDto responseDto = authService.singup(email, password, nickname);

        //then
        assertEquals(email, responseDto.getEmail());
        assertEquals(nickname, responseDto.getNickname());
        assertEquals("testToken", responseDto.getBearerToken());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
