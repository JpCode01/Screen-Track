package com.jpcode.screentrack.media;

public enum MediaType {
    MOVIE,
    SERIES;

    public static MediaType from(String type) {

        return switch (type.toLowerCase()) {
            case "movie" -> MOVIE;
            case "series" -> SERIES;
            default -> throw new IllegalArgumentException("Unknown media type");
        };
    }
}
