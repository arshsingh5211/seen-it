package com.arsh.seenit.service;

import com.arsh.seenit.dto.WatchlistItemDto;
import com.arsh.seenit.model.ContentMetadata;
import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.repository.ContentMetadataRepository;
import com.arsh.seenit.repository.WatchedItemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WatchService {
    private static final Logger log = LoggerFactory.getLogger(WatchService.class);
    private final ServiceProperties serviceProperties;
    private final ContentMetadataRepository contentMetadataRepository;
    private final WatchedItemRepository watchedItemRepository;

    public WatchedItem addToWatchlist(String imdbId) {
        JsonNode response = fetchOmdbDetails(imdbId);
        saveContent(response);
        return saveWatched(imdbId);
    }

    private JsonNode fetchOmdbDetails(String imdbId) {
        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("i", imdbId)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        if (response == null || "False".equals(response.get("Response").asString())) {
            throw new RuntimeException("OMDb content not found for imdbId: " + imdbId);
        }
        return response;
    }

    private ContentMetadata saveContent(JsonNode response) {
        ContentMetadata metadata = new ContentMetadata();

        metadata.setImdbId(response.get("imdbID").asString());
        metadata.setTitle(response.get("Title").asString());
        metadata.setYear(response.get("Year").asString());
        metadata.setRated(response.get("Rated").asString());
        metadata.setReleased(response.get("Released").asString());
        metadata.setRuntime(response.get("Runtime").asString());
        metadata.setGenre(response.get("Genre").asString());
        metadata.setDirector(response.get("Director").asString());
        metadata.setActors(response.get("Actors").asString());
        metadata.setPlot(response.get("Plot").asString());
        metadata.setLanguage(response.get("Language").asString());
        metadata.setType(response.get("Type").asString());
        metadata.setPosterUrl(response.get("Poster").asString());
        metadata.setImdbRating(response.get("imdbRating").asString());
        metadata.setMetascore(response.get("Metascore").asString());

        ContentMetadata saved = contentMetadataRepository.save(metadata);
        log.info("Saved metadata for imdbId={} title={}", saved.getImdbId(), saved.getTitle());
        return saved;
    }

    private WatchedItem saveWatched(String imdbId) {
        WatchedItem watchedItem = new WatchedItem();
        watchedItem.setImdbId(imdbId);
        return watchedItemRepository.save(watchedItem);
    }


    public List<WatchlistItemDto> getWatchList() {
        List<WatchedItem> watchedItems = StreamSupport
                .stream(watchedItemRepository.findAll().spliterator(), false)
                .toList();

        List<String> imdbIds = watchedItems.stream()
                .map(WatchedItem::getImdbId)
                .distinct()
                .toList();

        Map<String, ContentMetadata> metadataByImdbId = new HashMap<>();
        contentMetadataRepository.findAllById(imdbIds)
                .forEach(metadata -> metadataByImdbId.put(metadata.getImdbId(), metadata));

        List<WatchlistItemDto> watchList = new ArrayList<>();

        for (WatchedItem item : watchedItems) {
            ContentMetadata metadata = metadataByImdbId.get(item.getImdbId());

            if (metadata == null) {
                throw new RuntimeException("Missing metadata for imdbId: " + item.getImdbId());
            }
            // TODO: if metadata is missing:
            // 1. call OMDb API using imdbId
            // 2. save ContentMetadata to DB
            // 3. use fetched metadata instead of throwing

            watchList.add(new WatchlistItemDto(
                    item.getWatchedItemId(),
                    item.getImdbId(),
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
                    metadata.getType(),
                    metadata.getPosterUrl(),
                    metadata.getImdbRating(),
                    metadata.getMetascore(),
                    item.getRating(),
                    item.getReview(),
                    item.getWatchedAt(),
                    item.getReviewedAt()
            ));
        }
        return watchList;
    }
}