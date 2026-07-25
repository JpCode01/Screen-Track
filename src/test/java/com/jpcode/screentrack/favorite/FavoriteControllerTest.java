package com.jpcode.screentrack.favorite;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpcode.screentrack.exception.GlobalExceptionHandler;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.UserRepository;
import com.jpcode.screentrack.favorite.dto.CreateFavoriteRequestDto;
import com.jpcode.screentrack.favorite.dto.FavoriteResponseDto;
import com.jpcode.screentrack.favorite.dto.UpdateFavoriteRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private JwtService jwtService;


    private FavoriteResponseDto createResponse() {
        return new FavoriteResponseDto(
                1L,
                1,
                "tt0372784",
                "Batman Begins",
                "MOVIE",
                "poster.jpg"
        );
    }


    @Test
    void createFavoriteWhenRequestIsValidShouldReturnCreated() throws Exception {

        when(favoriteService.createFavorite(any()))
                .thenReturn(createResponse());


        CreateFavoriteRequestDto dto =
                new CreateFavoriteRequestDto(
                        "tt0372784"
                );


        mockMvc.perform(post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.position")
                        .value(1))
                .andExpect(jsonPath("$.imdbId")
                        .value("tt0372784"))
                .andExpect(jsonPath("$.title")
                        .value("Batman Begins"))
                .andExpect(jsonPath("$.type")
                        .value("MOVIE"));
    }


    @Test
    void updateFavoriteWhenRequestIsValidShouldReturnUpdatedFavorite() throws Exception {

        when(favoriteService.updateFavorite(any(), any()))
                .thenReturn(createResponse());


        UpdateFavoriteRequestDto dto =
                new UpdateFavoriteRequestDto(2);


        mockMvc.perform(put("/favorites/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position")
                        .value(1));
    }


    @Test
    void deleteFavoriteShouldReturnNoContent() throws Exception {

        doNothing()
                .when(favoriteService)
                .delete(1L);


        mockMvc.perform(delete("/favorites/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    void getFavoriteMoviesWhenUserExistsShouldReturnFavorites() throws Exception {

        User user = mock(User.class);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteService.findMovieByUser(user))
                .thenReturn(List.of(createResponse()));


        mockMvc.perform(get("/favorites/john/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Batman Begins"))
                .andExpect(jsonPath("$[0].type")
                        .value("MOVIE"));
    }


    @Test
    void getFavoriteSeriesWhenUserExistsShouldReturnFavorites() throws Exception {

        User user = mock(User.class);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteService.findSeriesByUser(user))
                .thenReturn(List.of(createResponse()));


        mockMvc.perform(get("/favorites/john/series"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Batman Begins"));
    }


    @Test
    void getMyFavoriteMoviesShouldReturnFavorites() throws Exception {

        when(favoriteService.findMyMovieFavorites())
                .thenReturn(List.of(createResponse()));


        mockMvc.perform(get("/favorites/me/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imdbId")
                        .value("tt0372784"));
    }


    @Test
    void getMyFavoriteSeriesShouldReturnFavorites() throws Exception {

        when(favoriteService.findMySeriesFavorites())
                .thenReturn(List.of(createResponse()));


        mockMvc.perform(get("/favorites/me/series"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Batman Begins"));
    }
}