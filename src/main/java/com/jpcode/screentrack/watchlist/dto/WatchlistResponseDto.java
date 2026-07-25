package com.jpcode.screentrack.watchlist.dto;

public record WatchlistResponseDto(
        Long id,
        String imdbId,
        String title,
        String type,
        String poster

) {
}
