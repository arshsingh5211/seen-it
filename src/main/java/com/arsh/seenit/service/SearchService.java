package com.arsh.seenit.service;

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

    public List<SearchDto> search(String query) {

        String url = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("s", query)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

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
}