package com.jpcode.screentrack.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordDto(
    @NotBlank String currentPassword,
    @NotBlank String newPassword,
    @NotBlank String newPasswordConfirmation
) {
}
