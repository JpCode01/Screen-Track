package com.jpcode.screentrack.admin;

import com.jpcode.screentrack.exception.GlobalExceptionHandler;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.user.UserRepository;
import com.jpcode.screentrack.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void deactivateUserWhenRequestIsValidShouldReturnNoContent() throws Exception {

        doNothing()
                .when(userService)
                .desactivateUser(1L);


        mockMvc.perform(patch("/admin/users/1/deactivate"))
                .andExpect(status().isNoContent());
    }


    @Test
    void reactivateUserWhenRequestIsValidShouldReturnNoContent() throws Exception {

        doNothing()
                .when(userService)
                .reactivateUser(1L);


        mockMvc.perform(patch("/admin/users/1/reactivate"))
                .andExpect(status().isNoContent());
    }
}