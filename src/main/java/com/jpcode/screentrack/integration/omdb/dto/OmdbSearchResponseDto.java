package com.jpcode.screentrack.integration.omdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OmdbSearchResponseDto(
        @JsonProperty("Search")
        List<OmdbSearchResultDto> search,

        @JsonProperty("totalResults")
        String totalResults,

        @JsonProperty("Response")
        String response
) {

}
