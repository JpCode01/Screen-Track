    package com.jpcode.screentrack.favorite;
    
    import com.jpcode.screentrack.exception.BusinessRuleException;
    import com.jpcode.screentrack.favorite.dto.CreateFavoriteRequestDto;
    import com.jpcode.screentrack.favorite.dto.FavoriteResponseDto;
    import com.jpcode.screentrack.favorite.dto.UpdateFavoriteRequestDto;
    import com.jpcode.screentrack.media.Media;
    import com.jpcode.screentrack.media.MediaService;
    import com.jpcode.screentrack.media.MediaType;
    import com.jpcode.screentrack.security.AuthenticatedUserService;
    import com.jpcode.screentrack.user.User;
    import com.jpcode.screentrack.watchlist.WatchlistRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.stereotype.Service;
    
    import java.util.List;
    
    @Service
    public class FavoriteService {
        private final FavoriteRepository favoriteRepository;
        private final MediaService mediaService;
        private final AuthenticatedUserService authenticatedUserService;
        private final WatchlistRepository watchlistRepository;
    
        public FavoriteService(FavoriteRepository favoriteRepository, MediaService mediaService, AuthenticatedUserService authenticatedUserService, WatchlistRepository watchlistRepository) {
            this.favoriteRepository = favoriteRepository;
            this.mediaService = mediaService;
            this.authenticatedUserService = authenticatedUserService;
    
            this.watchlistRepository = watchlistRepository;
        }
    
        @Transactional
        public FavoriteResponseDto createFavorite(CreateFavoriteRequestDto dto) {
            User user = authenticatedUserService.getAuthenticatedUser();
            Media media = mediaService.findOrCreateByImdbId(dto.imdbId());
    
            favoriteRepository
                    .findByUserAndMedia(user, media)
                    .ifPresent(favorite -> {
                        throw new BusinessRuleException("Media already exists in favorites");
                    });
    
            long quantity = favoriteRepository.countByUserAndMediaType(user, media.getType());
    
            if (quantity >= 5) {
                throw new BusinessRuleException("You can only have 5 favorites of this type");
            }
    
            Favorite favorite = new Favorite(
                    (int) quantity + 1,
                    media,
                    user
            );
    
            favoriteRepository.save(favorite);
            watchlistRepository.deleteByUserAndMedia(user, media);
            return toResponse(favorite);
        }
    
        @Transactional
        public FavoriteResponseDto updateFavorite(Long favoriteId, UpdateFavoriteRequestDto dto) {
            User user = authenticatedUserService.getAuthenticatedUser();
            Favorite favorite = favoriteRepository.findByUserAndId(user, favoriteId)
                    .orElseThrow(() -> new BusinessRuleException("Favorite not found"));
    
            Integer oldPosition = favorite.getPosition();
            Integer requestedPosition = dto.position();
    
            if (requestedPosition < 1 || requestedPosition > 5) {
                throw new BusinessRuleException("Position must be between 1 and 5");
            }
    
            List<Favorite> favorites = favoriteRepository
                    .findByUserAndMediaTypeOrderByPositionAsc(user, favorite.getMediaType());
    
            int maxPosition = favorites.size();
            int newPosition = requestedPosition > maxPosition ? maxPosition : requestedPosition;
    
            if (oldPosition.equals(newPosition)) {
                return toResponse(favorite);
            }
    
            if (newPosition < oldPosition) {
                favorites.stream()
                        .filter(f -> !f.getId().equals(favorite.getId()) &&
                                f.getPosition() >= newPosition &&
                                f.getPosition() < oldPosition)
                        .forEach(f -> f.changePosition(f.getPosition() + 1));
            } else {
                favorites.stream()
                        .filter(f -> !f.getId().equals(favorite.getId()) &&
                                f.getPosition() <= newPosition &&
                                f.getPosition() > oldPosition)
                        .forEach(f -> f.changePosition(f.getPosition() - 1));
            }
    
            favorite.changePosition(newPosition);
            return toResponse(favorite);
        }
    
        @Transactional
        public void delete(Long favoriteId) {
            User user = authenticatedUserService.getAuthenticatedUser();
    
            Favorite favorite = favoriteRepository.findByUserAndId(user, favoriteId)
                    .orElseThrow(() -> new BusinessRuleException("Favorite not found"));
    
            Integer deletedPosition = favorite.getPosition();
            MediaType type = favorite.getMediaType();
    
            favoriteRepository.delete(favorite);
    
            List<Favorite> favorites = favoriteRepository
                    .findByUserAndMediaTypeOrderByPositionAsc(user, type);
    
            favorites.stream()
                    .filter(f -> f.getPosition() > deletedPosition)
                    .forEach(f -> f.changePosition(f.getPosition() - 1));
        }
    
        public List<FavoriteResponseDto> findMovieByUser(User user) {
            return favoriteRepository
                    .findByUserAndMediaTypeOrderByPositionAsc(user, MediaType.MOVIE)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }
    
        public List<FavoriteResponseDto> findSeriesByUser(User user) {
            return favoriteRepository
                    .findByUserAndMediaTypeOrderByPositionAsc(user, MediaType.SERIES)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }
    
        public FavoriteResponseDto toResponse(Favorite favorite) {
            Media media = favorite.getMedia();
    
            return new FavoriteResponseDto(
                    favorite.getId(),
                    favorite.getPosition(),
                    media.getImdbId(),
                    media.getTitle(),
                    media.getType().name(),
                    media.getPoster()
            );
        }
    
        public List<FavoriteResponseDto> findMyMovieFavorites() {
            User user = authenticatedUserService.getAuthenticatedUser();
    
            return favoriteRepository
                    .findByUserAndMediaTypeOrderByPositionAsc(user, MediaType.MOVIE)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }
    
        public List<FavoriteResponseDto> findMySeriesFavorites() {
            User user = authenticatedUserService.getAuthenticatedUser();
    
            return favoriteRepository
                    .findByUserAndMediaTypeOrderByPositionAsc(user, MediaType.SERIES)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }
    }