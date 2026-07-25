    package com.jpcode.screentrack.media.dto;
    
    public record MediaSearchResponseDto(
            String title,
            String year,
            String imdbId,
            String type,
            String poster
    ) {
    }
