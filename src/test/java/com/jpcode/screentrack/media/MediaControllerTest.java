package com.jpcode.screentrack.media;

import com.jpcode.screentrack.exception.GlobalExceptionHandler;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.media.dto.MediaSearchResponseDto;
import com.jpcode.screentrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private MediaService mediaService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void searchWhenTitleExistsShouldReturnMediaList() throws Exception {

        MediaSearchResponseDto media =
                new MediaSearchResponseDto(
                        "Batman Begins",
                        "2005",
                        "tt0372784",
                        "movie",
                        "poster.jpg"
                );


        when(mediaService.search("batman"))
                .thenReturn(List.of(media));


        mockMvc.perform(get("/medias/search")
                        .param("title", "batman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Batman Begins"))
                .andExpect(jsonPath("$[0].year")
                        .value("2005"))
                .andExpect(jsonPath("$[0].imdbId")
                        .value("tt0372784"))
                .andExpect(jsonPath("$[0].type")
                        .value("movie"))
                .andExpect(jsonPath("$[0].poster")
                        .value("poster.jpg"));
    }


    @Test
    void getByImdbIdWhenMediaExistsShouldReturnMedia() throws Exception {

        Media media = mock(Media.class);


        when(media.getImdbId())
                .thenReturn("tt0372784");

        when(media.getTitle())
                .thenReturn("Batman Begins");

        when(media.getYear())
                .thenReturn("2005");

        when(media.getType())
                .thenReturn(MediaType.MOVIE);

        when(media.getPoster())
                .thenReturn("poster.jpg");

        when(media.getPlot())
                .thenReturn("Bruce Wayne becomes Batman.");

        when(media.getDirector())
                .thenReturn("Christopher Nolan");

        when(media.getActors())
                .thenReturn("Christian Bale");

        when(media.getGenre())
                .thenReturn("Action");


        when(mediaService.findOrCreateByImdbId("tt0372784"))
                .thenReturn(media);


        mockMvc.perform(get("/medias/tt0372784"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imdbId")
                        .value("tt0372784"))
                .andExpect(jsonPath("$.title")
                        .value("Batman Begins"))
                .andExpect(jsonPath("$.year")
                        .value("2005"))
                .andExpect(jsonPath("$.type")
                        .value("MOVIE"))
                .andExpect(jsonPath("$.poster")
                        .value("poster.jpg"))
                .andExpect(jsonPath("$.plot")
                        .value("Bruce Wayne becomes Batman."))
                .andExpect(jsonPath("$.director")
                        .value("Christopher Nolan"))
                .andExpect(jsonPath("$.actors")
                        .value("Christian Bale"))
                .andExpect(jsonPath("$.genre")
                        .value("Action"));
    }
}