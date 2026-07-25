package com.jpcode.screentrack.feed;

import com.jpcode.screentrack.feed.dto.FeedReviewResponseDto;
import com.jpcode.screentrack.feed.dto.MediaFeedDto;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.review.Review;
import com.jpcode.screentrack.review.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FeedService {
    private final ReviewRepository reviewRepository;

    public FeedService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Page<FeedReviewResponseDto> findReviews(Pageable pageable) {
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toFeedReviewResponseDto);
    }

    private FeedReviewResponseDto toFeedReviewResponseDto(Review review) {
        return new FeedReviewResponseDto(
                review.getUser().getUserName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                toMediaFeedDto(review.getMedia())
        );
    }

    private MediaFeedDto  toMediaFeedDto(Media media) {
        return new MediaFeedDto(
                media.getImdbId(),
                media.getTitle(),
                media.getPoster(),
                media.getType().name(),
                media.getYear()
        );
    }
}
