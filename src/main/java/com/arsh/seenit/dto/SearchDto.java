package com.arsh.seenit.dto;

public record SearchDto(
        String imdbId,
        String title,
        String year,
        String type,
        String poster
) {}