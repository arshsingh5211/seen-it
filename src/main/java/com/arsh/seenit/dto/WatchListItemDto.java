package com.arsh.seenit.dto;

import java.time.LocalDateTime;

public record WatchListItemDto(
        Integer watchedItemId,
        String imdbId,
        String title,
        String year,
        String rated,
        String released,
        String runtime,
        String genre,
        String director,
        String actors,
        String plot,
        String language,
        String type,
        String posterUrl,
        String imdbRating,
        String metascore,
        Integer rating,
        String review,
        LocalDateTime watchedAt,
        LocalDateTime reviewedAt
) {}