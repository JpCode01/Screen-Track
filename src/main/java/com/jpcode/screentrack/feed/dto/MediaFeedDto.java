package com.jpcode.screentrack.feed.dto;

public record MediaFeedDto(
                           String imdbId,
                           String title,
                           String poster,
                           String type,
                           String year
) {
}
