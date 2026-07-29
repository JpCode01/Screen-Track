package com.jpcode.screentrack.integration.omdb;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbSearchResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbSearchResultDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OmdbService {
    private final OmdbClient omdbClient;
    public OmdbService(OmdbClient omdbClient) {
        this.omdbClient = omdbClient;
    }

    public OmdbResponseDto findByImdbId(String imdbId) {
        OmdbResponseDto response =
                omdbClient.findByImdbId(imdbId);

        if (response.response().equals("False")) {
            throw new BusinessRuleException("Media not found");
        }
        return response;
    }

    public List<OmdbSearchResultDto> searchByTitle(String title) {
        OmdbSearchResponseDto response =
                omdbClient.searchByTitle(title);
        if (response == null ||
            response.response().equalsIgnoreCase("False")) {
            throw new BusinessRuleException("Media not found");
        }
        return response.search();
    }
}
