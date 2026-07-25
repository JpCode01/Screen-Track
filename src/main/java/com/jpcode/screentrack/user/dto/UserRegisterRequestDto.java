package com.jpcode.screentrack.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequestDto(
                @NotBlank String email,
                @NotBlank String password,
                @NotBlank String name,
                @NotBlank String username,
                String bio
) {
}
