package com.jpcode.screentrack.media;

import com.jpcode.screentrack.integration.omdb.OmdbService;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbSearchResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbSearchResultDto;
import com.jpcode.screentrack.media.dto.MediaResponseDto;
import com.jpcode.screentrack.media.dto.MediaSearchResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;
    private final OmdbService omdbService;

    public MediaService(MediaRepository mediaRepository, OmdbService omdbService) {
        this.mediaRepository = mediaRepository;
        this.omdbService = omdbService;
    }

    @Transactional
    public Media findOrCreateByImdbId(String imdbId) {
        return mediaRepository.findByImdbId(imdbId)
                .orElseGet(() -> {
                    OmdbResponseDto response = 
                            omdbService.findByImdbId(imdbId);
                    Media media = new Media(response);
                    return mediaRepository.save(media);
                });
    }
    
    @Transactional
    public List<MediaSearchResponseDto> search(String title) {

        return omdbService.searchByTitle(title)
                .stream()
                .map(this::toMediaSearchResponseDto)
                .toList();
    }
    
    private MediaSearchResponseDto toMediaSearchResponseDto(OmdbSearchResultDto dto) {
        return new MediaSearchResponseDto(
                dto.title(),
                dto.year(),
                dto.imdbId(),
                dto.type(),
                dto.poster()
        );
    }
}
