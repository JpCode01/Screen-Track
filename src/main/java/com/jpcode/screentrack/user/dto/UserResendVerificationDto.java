package com.jpcode.screentrack.user.dto;

import jakarta.validation.constraints.Email;

public record UserResendVerificationDto(
        @Email
        String email
) {
}
