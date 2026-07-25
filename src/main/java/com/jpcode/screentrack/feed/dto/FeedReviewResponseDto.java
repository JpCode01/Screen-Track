package com.jpcode.screentrack.feed.dto;

import com.jpcode.screentrack.review.Rating;

import java.time.LocalDateTime;

public record FeedReviewResponseDto(
        String username,
        Rating rating,
        String comment,
        LocalDateTime createdAt,
        MediaFeedDto media
) {
}