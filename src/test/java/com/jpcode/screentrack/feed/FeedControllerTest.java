package com.jpcode.screentrack.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpcode.screentrack.exception.GlobalExceptionHandler;
import com.jpcode.screentrack.feed.dto.FeedReviewResponseDto;
import com.jpcode.screentrack.feed.dto.MediaFeedDto;
import com.jpcode.screentrack.review.Rating;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FeedController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FeedControllerTest {


    @Autowired
    private MockMvc mockMvc;


    private final ObjectMapper objectMapper = new ObjectMapper();


    @MockitoBean
    private FeedService feedService;


    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;


    @MockitoBean
    private JwtService jwtService;


    @MockitoBean
    private UserRepository userRepository;



    private FeedReviewResponseDto createResponse() {

        return new FeedReviewResponseDto(
                "john",
                Rating.FIVE,
                "Great movie",
                LocalDateTime.now(),
                new MediaFeedDto(
                        "tt0816692",
                        "Interstellar",
                        "poster",
                        "MOVIE",
                        "2014"
                )
        );
    }



    @Test
    void getReviewsShouldReturnFeedPage() throws Exception {


        Page<FeedReviewResponseDto> page =
                new PageImpl<>(
                        List.of(createResponse())
                );


        when(feedService.findReviews(any()))
                .thenReturn(page);



        mockMvc.perform(get("/feed/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username")
                        .value("john"))
                .andExpect(jsonPath("$.content[0].rating")
                        .value("FIVE"))
                .andExpect(jsonPath("$.content[0].media.title")
                        .value("Interstellar"))
                .andExpect(jsonPath("$.content[0].media.imdbId")
                        .value("tt0816692"));
    }


    @Test
    void getReviewsWithPaginationShouldReturnOk() throws Exception {


        when(feedService.findReviews(any()))
                .thenReturn(
                        new PageImpl<>(List.of(createResponse()))
                );


        mockMvc.perform(get("/feed/reviews?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content")
                        .isArray());
    }
}