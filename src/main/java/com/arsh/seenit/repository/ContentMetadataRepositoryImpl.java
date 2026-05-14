package com.arsh.seenit.repository;

import com.arsh.seenit.model.ContentMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentMetadataRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;

    public ContentMetadata upsert(ContentMetadata metadata) {
        String sql = """
                INSERT INTO content_metadata (
                    imdb_id,
                    title,
                    year,
                    rated,
                    released,
                    runtime,
                    genre,
                    director,
                    actors,
                    plot,
                    language,
                    media_type,
                    poster_url,
                    imdb_rating,
                    metascore,
                    last_fetched_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (imdb_id)
                DO UPDATE SET
                    title = EXCLUDED.title,
                    year = EXCLUDED.year,
                    rated = EXCLUDED.rated,
                    released = EXCLUDED.released,
                    runtime = EXCLUDED.runtime,
                    genre = EXCLUDED.genre,
                    director = EXCLUDED.director,
                    actors = EXCLUDED.actors,
                    plot = EXCLUDED.plot,
                    language = EXCLUDED.language,
                    media_type = EXCLUDED.media_type,
                    poster_url = EXCLUDED.poster_url,
                    imdb_rating = EXCLUDED.imdb_rating,
                    metascore = EXCLUDED.metascore,
                    last_fetched_at = CURRENT_TIMESTAMP
                """;

        jdbcTemplate.update(
                sql,
                metadata.getImdbId(),
                metadata.getTitle(),
                metadata.getYear(),
                metadata.getRated(),
                metadata.getReleased(),
                metadata.getRuntime(),
                metadata.getGenre(),
                metadata.getDirector(),
                metadata.getActors(),
                metadata.getPlot(),
                metadata.getLanguage(),
                metadata.getMediaType(),
                metadata.getPosterUrl(),
                metadata.getImdbRating(),
                metadata.getMetascore()
        );

        return metadata;
    }
}