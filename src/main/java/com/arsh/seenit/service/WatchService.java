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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WatchService {
    private static final Logger log = LoggerFactory.getLogger(WatchService.class);
    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;
    private final ContentMetadataRepository contentMetadataRepository;
    private final WatchedItemRepository watchedItemRepository;

    public WatchedItem addToWatchlist(String imdbId) {
        getOrSaveContent(imdbId);
        return saveWatched(imdbId);
    }

    private ContentMetadata getOrSaveContent(String imdbId) {
        return contentMetadataRepository.findById(imdbId)
                .orElseGet(() -> {
                    JsonNode response = fetchOmdbDetails(imdbId);
                    return saveContent(response);
                });
    }

    private ContentMetadata saveContent(JsonNode response) {
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
        metadata.setType(response.path("Type").asString());
        metadata.setPosterUrl(response.path("Poster").asString());
        metadata.setImdbRating(response.path("imdbRating").asString());
        metadata.setMetascore(response.path("Metascore").asString());

        ContentMetadata saved = contentMetadataRepository.save(metadata);
        // TODO: Consider adding Redis later as a short-term cache in front of OMDb/API lookups.
        // For now, content_metadata in Postgres acts as the persistent metadata cache.
        log.info("Saved metadata for imdbId={} title={}", saved.getImdbId(), saved.getTitle());

        return saved;
    }

    private JsonNode fetchOmdbDetails(String imdbId) {
        String uri = UriComponentsBuilder
                .fromUriString(serviceProperties.baseUrl())
                .queryParam("i", imdbId)
                .queryParam("apikey", serviceProperties.apiKey())
                .toUriString();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);

        if (response == null || "False".equals(response.path("Response").asString())) {
            throw new RuntimeException("OMDb content not found for imdbId: " + imdbId);
        }
        return response;
    }

    public WatchedItem rateAndReviewContent(Integer watchedItemId, Integer rating, String review) {

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        WatchedItem item = watchedItemRepository.findById(watchedItemId)
                .orElseThrow(() -> new RuntimeException("Watched item not found"));

        item.setRating(rating);
        item.setReview(review);
        item.setReviewedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        return watchedItemRepository.save(item);
    }

    private WatchedItem saveWatched(String imdbId) {
        return watchedItemRepository.findByImdbId(imdbId)
                .orElseGet(() -> {
                    WatchedItem watchedItem = new WatchedItem();
                    watchedItem.setImdbId(imdbId);

                    WatchedItem saved = watchedItemRepository.save(watchedItem);
                    log.info("Saved watched item for imdbId={}", saved.getImdbId());

                    return saved;
                });
    }

    @Transactional
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
                metadata = getOrSaveContent(item.getImdbId());
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

// TODO: Add user support so watchlists are scoped by userId instead of globally by imdbId.
// TODO: Add pagination for GET /watchlist before the list gets large.
// TODO: Add filtering/sorting for watchlist by rating, type, genre, watchedAt, reviewedAt.
// TODO: Add proper exception classes instead of RuntimeException.
// TODO: Add controller-level error handling with @ControllerAdvice.
// TODO: Add metadata refresh strategy for stale content_metadata records.
// TODO: Consider upsert behavior for content_metadata instead of plain save.
// TODO: Add tests for addToWatchlist, rateAndReviewContent, and getWatchList.
// TODO: Add validation DTOs so controllers do not accept raw entity models directly.
// TODO: Consider Redis later as short-term cache in front of OMDb if traffic/API limits justify it.