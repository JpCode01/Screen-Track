package com.jpcode.screentrack.integration.omdb;

import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbSearchResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OmdbClient {
    private final RestClient restClient;
    private final String apiKey;
    
    public OmdbClient(RestClient.Builder builder, @Value("${omdb.api.key}")
    String apiKey) {
        this.restClient = builder
                .baseUrl("https://www.omdbapi.com")
                .build();
        this.apiKey = apiKey;
    }

    public OmdbResponseDto findByImdbId(String imdbId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("i", imdbId)
                        .build())
                .retrieve()
                .body(OmdbResponseDto.class);
    }

    public OmdbSearchResponseDto searchByTitle(String title) {
        return restClient.get()
                .uri(uriBuilder -> 
                        uriBuilder
                                .queryParam("apikey", apiKey)
                                .queryParam("s", title)
                                .build()
                )
                .retrieve()
                .body(OmdbSearchResponseDto.class);
    }
    


}
