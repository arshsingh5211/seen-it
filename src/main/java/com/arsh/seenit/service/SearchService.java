package com.arsh.seenit.service;

import com.arsh.seenit.dto.SearchDetailsDto;
import com.arsh.seenit.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
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

        if (response == null || !"True".equals(response.path("Response").asString())) {
            throw new RuntimeException("Invalid OMDb response for id: " + id);
        }

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