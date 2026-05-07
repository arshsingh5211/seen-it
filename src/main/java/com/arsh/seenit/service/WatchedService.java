package com.arsh.seenit.service;

import com.arsh.seenit.dto.SearchDetailsDto;
import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.repository.WatchedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class WatchedService {

    private final WatchedItemRepository repository;
    private final ServiceProperties serviceProperties;

    public WatchedItem save(WatchedItem watchedItem) {
        return repository.save(watchedItem);
    }

    public WatchedItem getWatchedItem(String id) {
        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("i", id)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        if (response != null) {
            return new WatchedItem(
                    response.get("imdbID").asString(),

            );
        }
    }

    public Iterable<WatchedItem> findAll() {
        return repository.findAll();
    }

    public WatchedItem update(String id, Integer rating, String review) {
        WatchedItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        item.setRating(rating);
        item.setReview(review);

        return repository.save(item);
    }
}