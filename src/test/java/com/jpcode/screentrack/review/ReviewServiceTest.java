package com.jpcode.screentrack.review;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaService;
import com.jpcode.screentrack.media.MediaType;
import com.jpcode.screentrack.review.dto.CreateReviewRequestDto;
import com.jpcode.screentrack.review.dto.UpdateReviewRequestDto;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.Role;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.watchlist.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private WatchlistRepository watchlistRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Media media;

    @BeforeEach
    void setUp() {
        user = new User();

        OmdbResponseDto dto = new OmdbResponseDto(
                "Interstellar",
                "2014",
                "tt0816692",
                MediaType.MOVIE.name(),
                "poster",
                "Plot",
                "Christopher Nolan",
                "Matthew McConaughey",
                "Adventure",
                "True"
        );
        media = new Media(dto);
    }

    @Test
    void createWhenValidRequestShouldCreateReview() {
        // Arrange
        CreateReviewRequestDto dto = new CreateReviewRequestDto(
                "tt0816692",
                Rating.FIVE,
                "Excellent movie"
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692"))
                .thenReturn(media);
        
        when(reviewRepository.findByUserAndMedia(user, media))
                .thenReturn(Optional.empty());
        // Act

        var response = reviewService.create(dto);
        // Assert
        assertThat(response)
                .isNotNull();

        assertThat(response.imdbId())
                .isEqualTo("tt0816692");

        assertThat(response.comment())
                .isEqualTo("Excellent movie");

        verify(reviewRepository)
                .save(any(Review.class));
    }

    @Test
    void createWhenReviewAlreadyExistsShouldThrowBusinessRuleException() {
        // Arrange
        CreateReviewRequestDto dto = new CreateReviewRequestDto(
                "tt0816692",
                Rating.FIVE,
                "Excellent movie"
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692"))
                .thenReturn(media);

        when(reviewRepository.findByUserAndMedia(user, media))
                .thenReturn(Optional.of(new Review(
                        Rating.FIVE,
                        "Already exists",
                        user,
                        media
                )));
        // Act and Assert

        assertThatThrownBy(() ->
                reviewService.create(dto))
                .isInstanceOf(BusinessRuleException.class)
                        .hasMessage("Review already exists");
        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void updateWhenUserOwnsReviewShouldUpdate() {
        // Assert
        Review review = new Review(
                Rating.FOUR,
                "Good movie",
                user,
                media
        );

        UpdateReviewRequestDto dto = new UpdateReviewRequestDto(
                Rating.FIVE,
                "Changed my mind"
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        // Act

        var response =
                reviewService.update(1L, dto);

        // Assert

        assertThat(response.rating())
                .isEqualTo(5.0);

        assertThat(response.comment())
                .isEqualTo("Changed my mind");
    }

    @Test
    void updateWhenReviewDoesNotBelongToUserShouldThrowBusinessRuleException() {
        // Assert
        User anotherUser = new User();

        Review review = new Review(
                Rating.FOUR,
                "Good movie",
                anotherUser,
                media
        );
        UpdateReviewRequestDto dto = new UpdateReviewRequestDto(
                Rating.FIVE,
                "Changed my mind"
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        // Act and Assert

        assertThatThrownBy(() ->
                reviewService.update(1L, dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("You cannot edit this review");
    }

    @Test
    void deleteOwnReviewWhenUserOwnsReviewShouldDelete() {
        // Arrange
        Review review = new Review(
                Rating.FIVE,
                "Great",
                user,
                media
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));
        // Act
        reviewService.delete(1L);
        // Assert
        verify(reviewRepository)
                .delete(review);
    }

    @Test
    void deleteOwnReviewWhenReviewDoesNotBelongToUserShouldThrowException() {
        // Assert
        User anotherUser = new User();

        Review review = new Review(
                Rating.FOUR,
                "Good movie",
                anotherUser,
                media
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        // Act and Assert

        assertThatThrownBy(() ->
                reviewService.deleteOwnReview(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("You cannot delete this review");

        verify(reviewRepository, never())
                .delete(any());
    }
    @Test
    void deleteWhenUserIsAdminShouldDeleteAnotherUsersReview() {

        User anotherUser = new User();

        User admin = new User();

        ReflectionTestUtils.setField(
                admin,
                "role",
                Role.ADMIN
        );

        Review review = new Review(
                Rating.FIVE,
                "Great",
                anotherUser,
                media
        );

        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(admin);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));


        reviewService.delete(1L);


        verify(reviewRepository)
                .delete(review);
    }

    @Test
    void getByIdWhenReviewExistsShouldReturnReview() {

        Review review = new Review(
                Rating.FIVE,
                "Amazing",
                user,
                media
        );

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));


        var response = reviewService.getById(1L);


        assertThat(response)
                .isNotNull();

        assertThat(response.comment())
                .isEqualTo("Amazing");

        assertThat(response.imdbId())
                .isEqualTo("tt0816692");
    }

    @Test
    void getByIdWhenReviewDoesNotExistShouldThrowException() {

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() ->
                reviewService.getById(1L)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Review not found");
    }

    @Test
    void findMyReviewsShouldReturnAuthenticatedUserReviews() {

        Review review = new Review(
                Rating.FIVE,
                "Perfect",
                user,
                media
        );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(reviewRepository.findByUser(user))
                .thenReturn(java.util.List.of(review));


        var response =
                reviewService.findMyReviews();


        assertThat(response)
                .hasSize(1);

        assertThat(response.get(0).rating())
                .isEqualTo(5.0);
    }



}