package com.jpcode.screentrack.review.dto;

import com.jpcode.screentrack.review.Rating;

public record CreateReviewRequestDto(
        String imdbId,
        Rating rating,
        String comment
) {
}
