package com.jpcode.screentrack.favorite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateFavoriteRequestDto(
        @NotBlank
        String imdbId
) {
}
