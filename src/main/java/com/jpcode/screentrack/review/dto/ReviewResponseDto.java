package com.jpcode.screentrack.review.dto;

public record ReviewResponseDto(
        Long id,
        String username,
        String imdbId,
        String title,
        Double rating,
        String comment
) {
}
