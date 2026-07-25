package com.jpcode.screentrack.media;

import com.jpcode.screentrack.media.dto.MediaResponseDto;
import com.jpcode.screentrack.media.dto.MediaSearchResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medias")
public class MediaController {

    private final MediaService mediaService;


    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }
    
    @GetMapping("/search")
    public List<MediaSearchResponseDto> search(@RequestParam String title) {
        return mediaService.search(title);
    }

    @GetMapping("/{imdbId}")
    public MediaResponseDto getByImdbId(@PathVariable String imdbId) {
        var media = mediaService.findOrCreateByImdbId(imdbId);
        return new MediaResponseDto(media);
    }

}
