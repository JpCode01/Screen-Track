package com.jpcode.screentrack.favorite.dto;

import com.jpcode.screentrack.media.MediaType;

public record FavoriteResponseDto(        Long id,

                                          Integer position,

                                          String imdbId,

                                          String title,

                                          String type,

                                          String poster
) {
}
