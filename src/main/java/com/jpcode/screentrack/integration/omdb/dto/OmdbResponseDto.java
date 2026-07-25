package com.jpcode.screentrack.integration.omdb.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OmdbResponseDto(
        @JsonProperty("Title")
        String title,

        @JsonProperty("Year")
        String year,

        @JsonProperty("imdbID")
        String imdbId,

        @JsonProperty("Type")
        String type,

        @JsonProperty("Poster")
        String poster,

        @JsonProperty("Plot")
        String plot,

        @JsonProperty("Director")
        String director,

        @JsonProperty("Actors")
        String actors,

        @JsonProperty("Genre")
        String genre,


        @JsonProperty("Response")
        String response
) {
}