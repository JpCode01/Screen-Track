package com.jpcode.screentrack.media.dto;

import com.jpcode.screentrack.media.Media;

public record MediaResponseDto(
        String title,
        String year,
        String imdbId,
        String type,
        String poster,
        String plot,
        String director,
        String actors,
        String genre
) {
    public MediaResponseDto(Media media) {
        this(
                media.getTitle(),
                media.getYear(),
                media.getImdbId(),
                media.getType().name(),
                media.getPoster(),
                media.getPlot(),
                media.getDirector(),
                media.getActors(),
                media.getGenre()
        );
    }
}
