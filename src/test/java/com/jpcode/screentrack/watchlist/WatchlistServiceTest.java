package com.jpcode.screentrack.watchlist;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.favorite.FavoriteRepository;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaService;
import com.jpcode.screentrack.media.MediaType;
import com.jpcode.screentrack.review.ReviewRepository;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.UserRepository;
import com.jpcode.screentrack.watchlist.dto.CreateWatchlistRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WatchlistService watchlistService;

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
    void createWhenValidRequestShouldCreateWatchlist() {
        // Arrange
        CreateWatchlistRequestDto dto = new CreateWatchlistRequestDto("tt0816692");

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692"))
                .thenReturn(media);
        
        when(watchlistRepository.existsByUserAndMedia(user, media))
                .thenReturn(false);

        when(favoriteRepository.existsByUserAndMedia(user, media))
        .thenReturn(false);

        when(reviewRepository.existsByUserAndMedia(user, media))
                .thenReturn(false);
        // Act
        var response = watchlistService.create(dto);

        // Assert
        assertThat(response)
                .isNotNull();

        assertThat(response.imdbId())
                .isEqualTo("tt0816692");

        verify(watchlistRepository)
                .save(any(Watchlist.class));
    }

    @Test
    void createWhenMediaAlreadyExistsInWatchlistShouldThrowBusinessRuleException() {
        // Arrange
        CreateWatchlistRequestDto dto = new CreateWatchlistRequestDto("tt0816692");

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        
        when(mediaService.findOrCreateByImdbId("tt0816692"))
                .thenReturn(media);

        when(watchlistRepository.existsByUserAndMedia(user, media))
                .thenReturn(true);

        // Act and Assert
        assertThatThrownBy(() ->
                watchlistService.create(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Media already exists in watchlist");

        verify(watchlistRepository, never())
                .save(any());
    }

    @Test
    void createWhenMediaAlreadyExistsInFavoritesShouldThrowBusinessRuleException() {
        // Assert
        CreateWatchlistRequestDto dto = new CreateWatchlistRequestDto("tt0816692");

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692"))
                .thenReturn(media);

        when(watchlistRepository.existsByUserAndMedia(user, media))
                .thenReturn(false);

        when(favoriteRepository.existsByUserAndMedia(user, media))
                .thenReturn(true);


        // Act and Assert

        assertThatThrownBy(() ->
                watchlistService.create(dto))
        .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Media already exists in favorites");

        verify(watchlistRepository, never())
        .save(any());
    }

    @Test
    void createWhenMediaAlreadyReviewedShouldThrowBusinessRuleException() {


        CreateWatchlistRequestDto dto =
                new CreateWatchlistRequestDto("tt0816692");


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(mediaService.findOrCreateByImdbId("tt0816692"))
                .thenReturn(media);


        when(watchlistRepository.existsByUserAndMedia(user, media))
                .thenReturn(false);


        when(favoriteRepository.existsByUserAndMedia(user, media))
                .thenReturn(false);


        when(reviewRepository.existsByUserAndMedia(user, media))
                .thenReturn(true);



        assertThatThrownBy(() ->
                watchlistService.create(dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Media already reviewed");


        verify(watchlistRepository, never())
                .save(any());

    }

    @Test
    void deleteWhenWatchlistExistsShouldDelete() {
        // Assert

        Watchlist watchlist = new Watchlist(user, media);
        
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        
        when(watchlistRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.of(watchlist));

        // Act
        watchlistService.delete(1L);
        // Assert
        verify(watchlistRepository).delete(watchlist);
    }

    @Test
    void deleteWhenWatchlistDoesNotExistShouldThrowBusinessRuleException() {


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(watchlistRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.empty());



        assertThatThrownBy(() ->
                watchlistService.delete(1L)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Watchlist item not found");


        verify(watchlistRepository, never())
                .delete(any());

    }
}