package com.jpcode.screentrack.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpcode.screentrack.auth.dto.LoginRequestDto;
import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.exception.GlobalExceptionHandler;

import com.jpcode.screentrack.security.JwtService;

import com.jpcode.screentrack.security.dto.JwtDto;
import com.jpcode.screentrack.security.dto.JwtRefreshDto;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void loginWhenCredentialsAreValidShouldReturnTokens() throws Exception {

        User user = mock(User.class);

        when(user.getUsername()).thenReturn("user@email.com");

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateToken(user))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(user))
                .thenReturn("refresh-token");

        LoginRequestDto dto =
                new LoginRequestDto(
                        "user@email.com",
                        "123456"
                );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("refresh-token"));
    }

    @Test
    void refreshTokenWhenTokenIsValidShouldReturnNewTokens() throws Exception {

        User user = mock(User.class);

        when(user.getId())
                .thenReturn(1L);


        when(jwtService.verifyToken("refresh-token"))
                .thenReturn("1");


        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));


        when(jwtService.generateToken(user))
                .thenReturn("new-access-token");


        when(jwtService.generateRefreshToken(user))
                .thenReturn("new-refresh-token");


        JwtRefreshDto dto =
                new JwtRefreshDto("refresh-token");


        mockMvc.perform(post("/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("new-refresh-token"));
    }

    @Test
    void refreshTokenWhenTokenIsInvalidShouldThrowException() throws Exception {

        when(jwtService.verifyToken(any()))
                .thenThrow(new BusinessRuleException("An Error ocurred verifying the token"));


        JwtRefreshDto dto =
                new JwtRefreshDto("invalid-token");


        mockMvc.perform(post("/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}