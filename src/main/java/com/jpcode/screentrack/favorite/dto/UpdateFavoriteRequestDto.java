package com.jpcode.screentrack.favorite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateFavoriteRequestDto(
        @Min(value = 1, message = "Position must be between 1 and 5")
        @Max(value = 5, message = "Position must be between 1 and 5")
        @NotNull
        Integer position
) {
}
