package com.arsh.seenit.omdb;

import com.arsh.seenit.service.ServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class OmdbClient {

    private final ServiceProperties serviceProperties;
    private final RestTemplate restTemplate;

    public JsonNode getByImdbId(String imdbId) {

        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("i", imdbId)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        if (response == null || !"True".equals(response.path("Response").asString())) {
            throw new RuntimeException("OMDb content not found for imdbId: " + imdbId);
        }

        return response;
    }

    public JsonNode search(String query) {

        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("s", query)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        if (response == null) {
            throw new RuntimeException("OMDb search failed");
        }

        return response;
    }
}