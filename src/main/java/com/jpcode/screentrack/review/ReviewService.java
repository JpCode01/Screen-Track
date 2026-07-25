package com.jpcode.screentrack.review;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaService;
import com.jpcode.screentrack.review.dto.CreateReviewRequestDto;
import com.jpcode.screentrack.review.dto.ReviewResponseDto;
import com.jpcode.screentrack.review.dto.UpdateReviewRequestDto;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.watchlist.WatchlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MediaService mediaService;
    private final AuthenticatedUserService authenticatedUserService;
    private final WatchlistRepository watchlistRepository;

    public ReviewService(ReviewRepository reviewRepository, MediaService mediaService, AuthenticatedUserService authenticatedUserService, WatchlistRepository watchlistRepository) {
        this.reviewRepository = reviewRepository;
        this.mediaService = mediaService;
        this.authenticatedUserService = authenticatedUserService;
        this.watchlistRepository = watchlistRepository;
    }

    @Transactional
    public ReviewResponseDto create(CreateReviewRequestDto dto) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Media media = mediaService.findOrCreateByImdbId(dto.imdbId());

        reviewRepository.findByUserAndMedia(user, media)
                .ifPresent(review -> {
                    throw new BusinessRuleException("Review already exists");
                });
        Review review = new Review(
                dto.rating(),
                dto.comment(),
                user,
                media
        );
        reviewRepository.save(review);
        watchlistRepository.deleteByUserAndMedia(user, media);
        return toReviewResponseDto(review);
    }

    @Transactional
    public ReviewResponseDto update(Long reviewId, UpdateReviewRequestDto dto) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new BusinessRuleException("Review not found"));

        if (!review.getUser().equals(user)) {
            throw new BusinessRuleException("You cannot edit this review");
        }

        review.edit(dto.rating(), dto.comment());
        return toReviewResponseDto(review);
    }

    @Transactional
    public void delete(Long reviewId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(
                        () -> new BusinessRuleException("Review not found")
                );

        if (!review.getUser().equals(user)
        && !user.isAdmin()) {
            throw new BusinessRuleException("You cannot delete this review");
        }
        reviewRepository.delete(review);
    }

    @Transactional
    public ReviewResponseDto getById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new BusinessRuleException("Review not found"));

        return toReviewResponseDto(review);
    }

    @Transactional
    public List<ReviewResponseDto> findByMedia(String imdbId) {
        Media media = mediaService.findOrCreateByImdbId(imdbId);

        return reviewRepository.findByMedia(media)
                .stream()
                .map(this::toReviewResponseDto)
                .toList();
    }

    private ReviewResponseDto toReviewResponseDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getUser().getUsername(),
                review.getMedia().getImdbId(),
                review.getMedia().getTitle(),
                review.getRating().getValue(),
                review.getComment()
        );
    }
    
    @Transactional
    public List<ReviewResponseDto> findMyReviews() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return reviewRepository.findByUser(user)
                .stream()
                .map(this::toReviewResponseDto)
                .toList();
    }


    @Transactional
    public void deleteOwnReview(Long reviewId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new BusinessRuleException("Review not found"));
        if (!review.getUser().equals(user)) {
            throw new BusinessRuleException("You cannot delete this review");
        }

        reviewRepository.delete(review);
    }

}
