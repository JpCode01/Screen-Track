package com.jpcode.screentrack.user.dto;

import com.jpcode.screentrack.user.User;

public record UserResponseDto(
        Long id,
        String name,
        String username,
        String bio
) {
    public UserResponseDto(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getUserName(),
                user.getBio()
        );
    }
}
