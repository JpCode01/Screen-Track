package com.jpcode.screentrack.watchlist.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWatchlistRequestDto(
        @NotBlank
        String imdbId
) {
}
