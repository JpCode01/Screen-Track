package com.jpcode.screentrack.media;

import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import jakarta.persistence.*;

@Entity
@Table(name = "medias")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private String imdbId;
    @Column(nullable = false)
    private String title;
    private String year;
    @Enumerated(EnumType.STRING)
    private MediaType type;
    private String poster;
    @Column(columnDefinition = "TEXT")
    private String plot;

    private String director;
    @Column(columnDefinition = "TEXT")
    private String actors;

    private String genre;

    public String getPlot() {
        return plot;
    }

    public String getDirector() {
        return director;
    }

    public String getActors() {
        return actors;
    }

    public String getGenre() {
        return genre;
    }

    @Deprecated
    public Media() {}

    public Media(OmdbResponseDto dto) {
        this.imdbId = dto.imdbId();
        this.title = dto.title();
        this.year = dto.year();
        this.type = MediaType.from(dto.type());
        this.poster = dto.poster();
        this.plot = dto.plot();
        this.director = dto.director();
        this.actors = dto.actors();
        this.genre = dto.genre();
    }

    public long getId() {
        return id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public MediaType getType() {
        return type;
    }

    public String getPoster() {
        return poster;
    }
}
