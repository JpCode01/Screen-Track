package com.jpcode.screentrack.favorite;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.favorite.dto.CreateFavoriteRequestDto;
import com.jpcode.screentrack.favorite.dto.UpdateFavoriteRequestDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaService;
import com.jpcode.screentrack.media.MediaType;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.watchlist.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private MediaService mediaService;
    
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    
    @Mock
    private WatchlistRepository watchlistRepository;
    
    @InjectMocks
    private FavoriteService favoriteService;
    
    private User user;
    private Media media;

    @BeforeEach
    void setUp() {
        user = new User();
        media = createMedia();
    }

    @Test
    void createFavoriteWhenValidRequestShouldCreateFavorite() {
        // Arrange
        CreateFavoriteRequestDto dto = new CreateFavoriteRequestDto("tt0816692");

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692")).thenReturn(media);

        when(favoriteRepository.findByUserAndMedia(user, media)).thenReturn(Optional.empty());

        when(favoriteRepository.countByUserAndMediaType(user, MediaType.MOVIE))
                .thenReturn(0L);
        // Act
        var response = favoriteService.createFavorite(dto);
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.position()).isEqualTo(1);
        assertThat(response.imdbId()).isEqualTo("tt0816692");
        verify(favoriteRepository)
                .save(any(Favorite.class));
        verify(favoriteRepository).save(any(Favorite.class));
        verify(watchlistRepository).deleteByUserAndMedia(user, media);
    }

    @Test
    void createFavoriteWhenMediaAlreadyExistsShouldThrowBusssinesRuleException() {
        // Arrange
        CreateFavoriteRequestDto dto = new CreateFavoriteRequestDto("tt0816692");

        Favorite favorite = new Favorite(
                1,
                media,
                user
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692")).thenReturn(media);

        when(favoriteRepository.findByUserAndMedia(user, media))
                .thenReturn(Optional.of(favorite));

        // Act and Assert
        assertThatThrownBy(() ->
                favoriteService.createFavorite(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Media already exists in favorites");

        verify(favoriteRepository, never())
                .save(any());
    }

    @Test
    void createFavoriteWhenUserHasFiveFavoritesShouldThrowBussinessRuleException() {
        // Arrange
        CreateFavoriteRequestDto dto = new CreateFavoriteRequestDto("tt0816692");

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(mediaService.findOrCreateByImdbId("tt0816692")).thenReturn(media);

        when(favoriteRepository.findByUserAndMedia(user, media))
                .thenReturn(Optional.empty());
        when(favoriteRepository.countByUserAndMediaType(user, MediaType.MOVIE))
                .thenReturn(5L);
        // Act and Assert
        assertThatThrownBy(() ->
                favoriteService.createFavorite(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("You can only have 5 favorites of this type");

        verify(favoriteRepository, never())
                .save(any());
    }
    @Test
    void updateFavoriteWhenMovingPositionUpShouldReorderFavorites() {
        // Arrange

        Favorite favoriteToMove = new Favorite(
                2,
                media,
                user
        );


        Favorite favoritePositionOne = new Favorite(
                1,
                createMedia(),
                user
        );


        Favorite favoritePositionThree = new Favorite(
                3,
                createMedia(),
                user
        );

        ReflectionTestUtils.setField(
                favoriteToMove,
                "id",
                1L
        );

        ReflectionTestUtils.setField(
                favoritePositionOne,
                "id",
                2L
        );

        ReflectionTestUtils.setField(
                favoritePositionThree,
                "id",
                3L
        );

        UpdateFavoriteRequestDto dto = new UpdateFavoriteRequestDto(1);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        when(favoriteRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.of(favoriteToMove));

        when(favoriteRepository
                .findByUserAndMediaTypeOrderByPositionAsc(user, MediaType.MOVIE))
                .thenReturn(
                        List.of(
                                favoritePositionOne,
                                favoriteToMove,
                                favoritePositionThree
                        )
                );
        // Act
        var response = favoriteService.updateFavorite(1L, dto);
        // Assert
        assertThat(response.position()).isEqualTo(1);

        assertThat(favoritePositionOne.getPosition()).isEqualTo(2);

        verify(favoriteRepository, never())
                .save(any());
    }
    

    private Media createMedia() {

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

        return new Media(dto);
    }

    @Test
    void updateFavoriteWhenMovingPositionDownShouldReorderFavorites() {

        Favorite favoriteToMove = new Favorite(
                1,
                media,
                user
        );

        Favorite favoritePositionTwo = new Favorite(
                2,
                createMedia(),
                user
        );

        Favorite favoritePositionThree = new Favorite(
                3,
                createMedia(),
                user
        );


        ReflectionTestUtils.setField(
                favoriteToMove,
                "id",
                1L
        );

        ReflectionTestUtils.setField(
                favoritePositionTwo,
                "id",
                2L
        );

        ReflectionTestUtils.setField(
                favoritePositionThree,
                "id",
                3L
        );


        UpdateFavoriteRequestDto dto =
                new UpdateFavoriteRequestDto(3);


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(favoriteRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.of(favoriteToMove));


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.MOVIE
        ))
                .thenReturn(
                        List.of(
                                favoriteToMove,
                                favoritePositionTwo,
                                favoritePositionThree
                        )
                );


        var response =
                favoriteService.updateFavorite(1L, dto);


        assertThat(response.position())
                .isEqualTo(3);

        assertThat(favoritePositionTwo.getPosition())
                .isEqualTo(1);

        assertThat(favoritePositionThree.getPosition())
                .isEqualTo(2);
    }


    @Test
    void updateFavoriteWhenPositionIsInvalidShouldThrowException() {

        Favorite favorite = new Favorite(
                1,
                media,
                user
        );

        ReflectionTestUtils.setField(
                favorite,
                "id",
                1L
        );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(favoriteRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.of(favorite));


        UpdateFavoriteRequestDto dto =
                new UpdateFavoriteRequestDto(6);


        assertThatThrownBy(() ->
                favoriteService.updateFavorite(1L, dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Position must be between 1 and 5");
    }


    @Test
    void updateFavoriteWhenFavoriteDoesNotExistShouldThrowException() {

        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(favoriteRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() ->
                favoriteService.updateFavorite(
                        1L,
                        new UpdateFavoriteRequestDto(2)
                )
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Favorite not found");
    }


    @Test
    void updateFavoriteWhenPositionIsTheSameShouldReturnSameFavorite() {

        Favorite favorite = new Favorite(
                2,
                media,
                user
        );

        Favorite anotherFavorite = new Favorite(
                1,
                createMedia(),
                user
        );

        ReflectionTestUtils.setField(
                favorite,
                "id",
                1L
        );

        ReflectionTestUtils.setField(
                anotherFavorite,
                "id",
                2L
        );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(favoriteRepository.findByUserAndId(user, 1L))
                .thenReturn(Optional.of(favorite));


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.MOVIE
        ))
                .thenReturn(
                        List.of(
                                anotherFavorite,
                                favorite
                        )
                );


        var response =
                favoriteService.updateFavorite(
                        1L,
                        new UpdateFavoriteRequestDto(2)
                );


        assertThat(response.position())
                .isEqualTo(2);
    }

    @Test
    void deleteFavoriteWhenValidShouldDeleteAndReorderPositions() {

        Favorite favoriteToDelete =
                new Favorite(
                        2,
                        media,
                        user
                );

        Favorite favoriteAfterDelete =
                new Favorite(
                        3,
                        createMedia(),
                        user
                );


        ReflectionTestUtils.setField(
                favoriteToDelete,
                "id",
                1L
        );

        ReflectionTestUtils.setField(
                favoriteAfterDelete,
                "id",
                2L
        );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(favoriteRepository.findByUserAndId(user,1L))
                .thenReturn(Optional.of(favoriteToDelete));


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.MOVIE
        ))
                .thenReturn(
                        List.of(favoriteAfterDelete)
                );


        favoriteService.delete(1L);


        assertThat(favoriteAfterDelete.getPosition())
                .isEqualTo(2);


        verify(favoriteRepository)
                .delete(favoriteToDelete);
    }


    @Test
    void deleteFavoriteWhenFavoriteDoesNotExistShouldThrowException(){

        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(favoriteRepository.findByUserAndId(user,1L))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() ->
                favoriteService.delete(1L)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Favorite not found");
    }

    @Test
    void findMovieFavoritesShouldReturnFavorites(){

        Favorite favorite =
                new Favorite(
                        1,
                        media,
                        user
                );

        ReflectionTestUtils.setField(
                favorite,
                "id",
                1L
        );


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.MOVIE
        ))
                .thenReturn(List.of(favorite));


        var response =
                favoriteService.findMovieByUser(user);


        assertThat(response)
                .hasSize(1);

        assertThat(response.get(0).type())
                .isEqualTo("MOVIE");
    }



    @Test
    void findSeriesFavoritesShouldReturnFavorites(){

        Media series = createMedia();

        Favorite favorite =
                new Favorite(
                        1,
                        series,
                        user
                );


        ReflectionTestUtils.setField(
                favorite,
                "id",
                1L
        );


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.SERIES
        ))
                .thenReturn(List.of(favorite));


        var response =
                favoriteService.findSeriesByUser(user);


        assertThat(response)
                .hasSize(1);
    }



    @Test
    void findMyMovieFavoritesShouldReturnAuthenticatedUserFavorites(){

        Favorite favorite =
                new Favorite(
                        1,
                        media,
                        user
                );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.MOVIE
        ))
                .thenReturn(List.of(favorite));


        var response =
                favoriteService.findMyMovieFavorites();


        assertThat(response)
                .hasSize(1);
    }



    @Test
    void findMySeriesFavoritesShouldReturnAuthenticatedUserFavorites(){

        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        when(favoriteRepository.findByUserAndMediaTypeOrderByPositionAsc(
                user,
                MediaType.SERIES
        ))
                .thenReturn(List.of());


        var response =
                favoriteService.findMySeriesFavorites();


        assertThat(response)
                .isEmpty();
    }
}