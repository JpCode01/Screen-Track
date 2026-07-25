package com.jpcode.screentrack.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpcode.screentrack.exception.GlobalExceptionHandler;
import com.jpcode.screentrack.review.dto.CreateReviewRequestDto;
import com.jpcode.screentrack.review.dto.ReviewResponseDto;
import com.jpcode.screentrack.review.dto.UpdateReviewRequestDto;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private JwtService jwtService;
    
    @MockitoBean
    private UserRepository userRepository;


    private ReviewResponseDto createResponse() {
        return new ReviewResponseDto(
                1L,
                "john",
                "tt0372784",
                "Batman Begins",
                5.0,
                "Great movie"
        );
    }


    @Test
    void createReviewWhenRequestIsValidShouldReturnCreated() throws Exception {

        ReviewResponseDto response = createResponse();

        when(reviewService.create(any()))
                .thenReturn(response);


        CreateReviewRequestDto dto =
                new CreateReviewRequestDto(
                        "tt0372784",
                        Rating.FIVE,
                        "Great movie"
                );


        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.username")
                        .value("john"))
                .andExpect(jsonPath("$.imdbId")
                        .value("tt0372784"))
                .andExpect(jsonPath("$.title")
                        .value("Batman Begins"))
                .andExpect(jsonPath("$.rating")
                        .value(5.0))
                .andExpect(jsonPath("$.comment")
                        .value("Great movie"));
    }


    @Test
    void updateReviewWhenRequestIsValidShouldReturnUpdatedReview() throws Exception {

        ReviewResponseDto response = createResponse();

        when(reviewService.update(any(), any()))
                .thenReturn(response);


        UpdateReviewRequestDto dto =
                new UpdateReviewRequestDto(
                        Rating.FOUR,
                        "Updated comment"
                );


        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.rating")
                        .value(5.0));
    }


    @Test
    void getReviewByIdWhenReviewExistsShouldReturnReview() throws Exception {

        when(reviewService.getById(1L))
                .thenReturn(createResponse());


        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username")
                        .value("john"))
                .andExpect(jsonPath("$.title")
                        .value("Batman Begins"));
    }


    @Test
    void getReviewsByMediaShouldReturnReviewList() throws Exception {

        when(reviewService.findByMedia("tt0372784"))
                .thenReturn(List.of(createResponse()));


        mockMvc.perform(get("/reviews/media/tt0372784"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imdbId")
                        .value("tt0372784"))
                .andExpect(jsonPath("$[0].username")
                        .value("john"));
    }


    @Test
    void getMyReviewsShouldReturnReviewList() throws Exception {

        when(reviewService.findMyReviews())
                .thenReturn(List.of(createResponse()));


        mockMvc.perform(get("/reviews/my-reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username")
                        .value("john"));
    }


    @Test
    void deleteReviewShouldReturnNoContent() throws Exception {

        doNothing()
                .when(reviewService)
                .delete(1L);


        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    void deleteOwnReviewShouldReturnNoContent() throws Exception {

        doNothing()
                .when(reviewService)
                .deleteOwnReview(1L);


        mockMvc.perform(delete("/reviews/my-reviews/1"))
                .andExpect(status().isNoContent());
    }
}