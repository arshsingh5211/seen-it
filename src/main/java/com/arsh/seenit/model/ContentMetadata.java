package com.arsh.seenit.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("content_metadata")
public class ContentMetadata {

    @Id
    private String imdbId;

    private String title;
    private String year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String actors;
    private String plot;
    private String language;
    private String type;
    private String posterUrl;
    private String imdbRating;
    private String metascore;
    private LocalDateTime lastFetchedAt;
}