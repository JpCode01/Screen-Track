package com.jpcode.screentrack.watchlist;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.favorite.FavoriteRepository;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaService;
import com.jpcode.screentrack.review.ReviewRepository;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.UserRepository;
import com.jpcode.screentrack.watchlist.dto.CreateWatchlistRequestDto;
import com.jpcode.screentrack.watchlist.dto.WatchlistResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final MediaService mediaService;
    private final AuthenticatedUserService authenticatedUserService;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, MediaService mediaService, AuthenticatedUserService authenticatedUserService, FavoriteRepository favoriteRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.watchlistRepository = watchlistRepository;
        this.mediaService = mediaService;
        this.authenticatedUserService = authenticatedUserService;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WatchlistResponseDto create(CreateWatchlistRequestDto dto) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Media media = mediaService.findOrCreateByImdbId(dto.imdbId());

        if (watchlistRepository.existsByUserAndMedia(user, media)) {
            throw new BusinessRuleException("Media already exists in watchlist");

        }

        if (favoriteRepository.existsByUserAndMedia(user, media)) {
            throw new BusinessRuleException("Media already exists in favorites");
        }
        
        if (reviewRepository.existsByUserAndMedia(user, media)) {
            throw new BusinessRuleException("Media already reviewed");
        }

        Watchlist watchlist = new Watchlist(user, media);
        watchlistRepository.save(watchlist);
        return toResponse(watchlist);
    }

    public List<WatchlistResponseDto> findMyWatchList() {
        User user = authenticatedUserService.getAuthenticatedUser();

        return watchlistRepository
                .findByUserOrderByIdDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Watchlist watchlist = findByUserAndId(user, id);
        watchlistRepository.delete(watchlist);
    }

    private Watchlist findByUserAndId(User user, Long id) {
        return watchlistRepository
                .findByUserAndId(user, id)
                .orElseThrow(() ->
                        new BusinessRuleException("Watchlist item not found"));
    }

    private WatchlistResponseDto toResponse(Watchlist watchlist) {
        Media media = watchlist.getMedia();

        return new WatchlistResponseDto(
                watchlist.getId(),
                media.getImdbId(),
                media.getTitle(),
                media.getType().name(),
                media.getPoster()
        );
    }

    public List<WatchlistResponseDto> findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BusinessRuleException("User not found"));
        return watchlistRepository
                .findByUserOrderByIdDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
