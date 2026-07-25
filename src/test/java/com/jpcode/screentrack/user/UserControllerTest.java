package com.jpcode.screentrack.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.exception.GlobalExceptionHandler;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.security.JwtAuthenticationFilter;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.user.dto.UserDeactiveAccountDto;
import com.jpcode.screentrack.user.dto.UserResponseDto;
import com.jpcode.screentrack.user.dto.UserUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void findUserByUsernameWhenUserExistsShouldReturnUser() throws Exception {

        User user = mock(User.class);

        when(user.getId())
                .thenReturn(1L);

        when(user.getName())
                .thenReturn("John Doe");

        when(user.getUserName())
                .thenReturn("john");

        when(user.getBio())
                .thenReturn("My bio");


        when(userService.searchByUsername("john"))
                .thenReturn(user);


        mockMvc.perform(get("/users/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.name")
                        .value("John Doe"))
                .andExpect(jsonPath("$.username")
                        .value("john"))
                .andExpect(jsonPath("$.bio")
                        .value("My bio"));
    }


    @Test
    void updateUserWhenRequestIsValidShouldReturnUpdatedUser() throws Exception {

        User user = mock(User.class);

        when(user.getId())
                .thenReturn(1L);

        when(user.getName())
                .thenReturn("John Updated");

        when(user.getUserName())
                .thenReturn("john.updated");

        when(user.getBio())
                .thenReturn("My bio");


        when(userService.editProfile(any()))
                .thenReturn(user);


        UserUpdateRequestDto dto =
                new UserUpdateRequestDto(
                        "John Updated",
                        "john.updated"
                );


        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username")
                        .value("john.updated"));
    }

    @Test
    void deactivateAccountWhenRequestIsValidShouldReturnNoContent() throws Exception {

        UserDeactiveAccountDto dto =
                new UserDeactiveAccountDto("123456");


        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

    }


    @Test
    void findUserByUsernameWhenUserDoesNotExistShouldReturnNotFound() throws Exception {

        when(userService.searchByUsername("unknown"))
                .thenThrow(new BusinessRuleException("User not found"));


        mockMvc.perform(get("/users/unknown"))
                .andExpect(status().isBadRequest());
    }
}