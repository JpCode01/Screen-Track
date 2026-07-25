package com.jpcode.screentrack.media;

import static org.junit.jupiter.api.Assertions.*;

import com.jpcode.screentrack.integration.omdb.OmdbService;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbSearchResultDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private OmdbService omdbService;

    @InjectMocks
    private MediaService mediaService;


    @Test
    void findOrCreateByImdbIdWhenMediaExistsShouldReturnMedia() {

        Media media = mock(Media.class);

        when(mediaRepository.findByImdbId("tt0372784"))
                .thenReturn(Optional.of(media));


        Media result =
                mediaService.findOrCreateByImdbId("tt0372784");


        assertEquals(media, result);

        verify(mediaRepository)
                .findByImdbId("tt0372784");

        verifyNoInteractions(omdbService);

        verify(mediaRepository, never())
                .save(any());
    }


    @Test
    void findOrCreateByImdbIdWhenMediaDoesNotExistShouldCreateMedia() {

        OmdbResponseDto dto =
                new OmdbResponseDto(
                        "Batman Begins",
                        "2005",
                        "tt0372784",
                        "movie",
                        "poster.jpg",
                        "Bruce Wayne becomes Batman.",
                        "Christopher Nolan",
                        "Christian Bale",
                        "Action",
                        "True"
                );


        Media savedMedia = new Media(dto);


        when(mediaRepository.findByImdbId("tt0372784"))
                .thenReturn(Optional.empty());

        when(omdbService.findByImdbId("tt0372784"))
                .thenReturn(dto);

        when(mediaRepository.save(any(Media.class)))
                .thenReturn(savedMedia);



        Media result =
                mediaService.findOrCreateByImdbId("tt0372784");


        assertNotNull(result);

        verify(omdbService)
                .findByImdbId("tt0372784");

        verify(mediaRepository)
                .save(any(Media.class));
    }


    @Test
    void searchWhenTitleExistsShouldReturnMediaSearchResponseDto() {

        OmdbSearchResultDto dto =
                new OmdbSearchResultDto(
                        "Batman Begins",
                        "2005",
                        "tt0372784",
                        "movie",
                        "poster.jpg"
                );


        when(omdbService.searchByTitle("Batman"))
                .thenReturn(List.of(dto));


        var result =
                mediaService.search("Batman");


        assertEquals(1, result.size());

        assertEquals(
                "Batman Begins",
                result.get(0).title()
        );

        assertEquals(
                "tt0372784",
                result.get(0).imdbId()
        );

        assertEquals(
                "movie",
                result.get(0).type()
        );


        verify(omdbService)
                .searchByTitle("Batman");
    }
}