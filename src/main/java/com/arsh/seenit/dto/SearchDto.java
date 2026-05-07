package com.arsh.seenit.dto;

public record SearchDto(
        String imdbID,
        String title,
        String year,
        String type,
        String poster
) {}