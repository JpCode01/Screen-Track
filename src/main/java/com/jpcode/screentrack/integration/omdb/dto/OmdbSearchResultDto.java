package com.jpcode.screentrack.integration.omdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OmdbSearchResultDto(@JsonProperty("Title")
                                          String title,

                                  @JsonProperty("Year")
                                          String year,

                                  @JsonProperty("imdbID")
                                          String imdbId,

                                  @JsonProperty("Type")
                                          String type,

                                  @JsonProperty("Poster")
                                          String poster) {

}
