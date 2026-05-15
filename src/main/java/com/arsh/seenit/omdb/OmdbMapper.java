package com.arsh.seenit.omdb;

import com.arsh.seenit.dto.SearchDetailsDto;
import com.arsh.seenit.dto.SearchDto;
import com.arsh.seenit.model.ContentMetadata;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OmdbMapper {

    public ContentMetadata toContentMetadata(JsonNode response) {

        ContentMetadata metadata = new ContentMetadata();

        metadata.setImdbId(response.path("imdbID").asString());
        metadata.setTitle(response.path("Title").asString());
        metadata.setYear(response.path("Year").asString());
        metadata.setRated(response.path("Rated").asString());
        metadata.setReleased(response.path("Released").asString());
        metadata.setRuntime(response.path("Runtime").asString());
        metadata.setGenre(response.path("Genre").asString());
        metadata.setDirector(response.path("Director").asString());
        metadata.setActors(response.path("Actors").asString());
        metadata.setPlot(response.path("Plot").asString());
        metadata.setLanguage(response.path("Language").asString());
        metadata.setMediaType(response.path("Type").asString());
        metadata.setPosterUrl(response.path("Poster").asString());
        metadata.setImdbRating(response.path("imdbRating").asString());
        metadata.setMetascore(response.path("Metascore").asString());
        metadata.setLastFetchedAt(LocalDateTime.now());

        return metadata;
    }

    public List<SearchDto> toSearchResults(JsonNode response) {
        List<SearchDto> results = new ArrayList<>();

        if (response != null && response.has("Search")) {
            for (JsonNode node : response.get("Search")) {
                results.add(new SearchDto(
                        node.path("imdbID").asString(),
                        node.path("Title").asString(),
                        node.path("Year").asString(),
                        node.path("Type").asString(),
                        node.path("Poster").asString()
                ));
            }
        }

        return results;
    }

    public SearchDetailsDto toSearchDetailsDto(JsonNode response) {

        List<SearchDetailsDto.Rating> ratings = new ArrayList<>();

        if (response.has("Ratings")) {
            for (JsonNode r : response.get("Ratings")) {
                ratings.add(new SearchDetailsDto.Rating(
                        r.path("Source").asString(),
                        r.path("Value").asString()
                ));
            }
        }

        return new SearchDetailsDto(
                response.path("Title").asString(),
                response.path("Year").asString(),
                response.path("Rated").asString(),
                response.path("Released").asString(),
                response.path("Runtime").asString(),
                response.path("Genre").asString(),
                response.path("Director").asString(),
                response.path("Writer").asString(),
                response.path("Actors").asString(),
                response.path("Plot").asString(),
                response.path("Language").asString(),
                response.path("Country").asString(),
                response.path("Awards").asString(),
                response.path("Poster").asString(),
                ratings,
                response.path("Metascore").asString(),
                response.path("imdbRating").asString(),
                response.path("imdbVotes").asString(),
                response.path("imdbID").asString(),
                response.path("Type").asString(),
                response.path("BoxOffice").asString(),
                response.path("Response").asString()
        );
    }
}