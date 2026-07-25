package com.jpcode.screentrack.security.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtRefreshDto(
        @NotBlank String refreshToken
) {
}
