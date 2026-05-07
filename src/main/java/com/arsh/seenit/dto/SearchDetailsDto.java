package com.arsh.seenit.dto;

import java.util.List;

public record SearchDetailsDto(
        String Title,
        String Year,
        String Rated,
        String Released,
        String Runtime,
        String Genre,
        String Director,
        String Writer,
        String Actors,
        String Plot,
        String Language,
        String Country,
        String Awards,
        String Poster,
        List<Rating> Ratings,
        String Metascore,
        String imdbRating,
        String imdbVotes,
        String imdbID,
        String Type,
        String BoxOffice,
        String Response
) {
    public record Rating(
            String Source,
            String Value
    ) {}
}