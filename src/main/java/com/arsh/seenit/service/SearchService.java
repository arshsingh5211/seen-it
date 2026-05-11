package com.arsh.seenit.service;

import com.arsh.seenit.dto.SearchDetailsDto;
import com.arsh.seenit.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ServiceProperties serviceProperties;
    private final RestTemplate restTemplate;

    public List<SearchDto> search(String query) {

        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("s", query)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        List<SearchDto> results = new ArrayList<>();

        if (response != null && response.has("Search")) {
            for (JsonNode node : response.get("Search")) {
                results.add(new SearchDto(
                        node.get("imdbID").asString(),
                        node.get("Title").asString(),
                        node.get("Year").asString(),
                        node.get("Type").asString(),
                        node.get("Poster").asString()
                ));
            }
        }
        // todo: handle an empty response here somehow

        return results;
    }

    public SearchDetailsDto getDetails(String id) {
        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("i", id)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        if (response != null && "True".equals(response.get("Response").asString())) {
            List<SearchDetailsDto.Rating> ratings = new ArrayList<>();

            for (JsonNode r : response.get("Ratings")) {
                ratings.add(new SearchDetailsDto.Rating(
                        r.get("Source").asString(),
                        r.get("Value").asString()
                ));
            }

            return new SearchDetailsDto(
                    response.get("Title").asString(),
                    response.get("Year").asString(),
                    response.get("Rated").asString(),
                    response.get("Released").asString(),
                    response.get("Runtime").asString(),
                    response.get("Genre").asString(),
                    response.get("Director").asString(),
                    response.get("Writer").asString(),
                    response.get("Actors").asString(),
                    response.get("Plot").asString(),
                    response.get("Language").asString(),
                    response.get("Country").asString(),
                    response.get("Awards").asString(),
                    response.get("Poster").asString(),
                    ratings,
                    response.get("Metascore").asString(),
                    response.get("imdbRating").asString(),
                    response.get("imdbVotes").asString(),
                    response.get("imdbID").asString(),
                    response.get("Type").asString(),
                    response.get("BoxOffice").asString(),
                    response.get("Response").asString()
            );
        }
        throw new RuntimeException("Movie not found");
    }
}