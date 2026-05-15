package com.arsh.seenit.service;

import com.arsh.seenit.dto.ReviewResponse;
import com.arsh.seenit.dto.WatchListItemDto;
import com.arsh.seenit.model.ContentMetadata;
import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.omdb.OmdbClient;
import com.arsh.seenit.omdb.OmdbMapper;
import com.arsh.seenit.repository.ContentMetadataRepository;
import com.arsh.seenit.repository.WatchedItemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WatchService {
    private static final Logger log = LoggerFactory.getLogger(WatchService.class);
    private final ContentMetadataRepository contentMetadataRepository;
    private final WatchedItemRepository watchedItemRepository;
    private final OmdbClient omdbClient;
    private final OmdbMapper omdbMapper;

    @Transactional
    public WatchListItemDto addToWatchList(String imdbId) {
        ContentMetadata metadata = getOrSaveContent(imdbId);

        WatchedItem watchedItem = saveWatched(imdbId);

        return toWatchListItemDto(watchedItem, metadata);
    }

    public void removeFromWatchList(String imdbId) {
        Optional<WatchedItem> itemToDelete = watchedItemRepository.findByImdbId(imdbId);
        itemToDelete.ifPresent(watchedItemRepository::delete);
    }

    private ContentMetadata getOrSaveContent(String imdbId) {
        return contentMetadataRepository.findById(imdbId)
                .orElseGet(() -> {
                    JsonNode response = omdbClient.getByImdbId(imdbId);
                    return saveContent(response);
                });
    }

    private ContentMetadata saveContent(JsonNode response) {
        ContentMetadata metadata = omdbMapper.toContentMetadata(response);

        ContentMetadata saved = contentMetadataRepository.upsert(metadata);

        log.info("Saved object imdbId={}", saved.getImdbId());

        Optional<ContentMetadata> fetched =
                contentMetadataRepository.findById(saved.getImdbId());

        log.info("Found immediately after save? {}", fetched.isPresent());
        // TODO: Consider adding Redis later as a short-term cache in front of OMDb/API lookups.
        // For now, content_metadata in Postgres acts as the persistent metadata cache.
        log.info("Saved metadata for imdbId={} title={}", saved.getImdbId(), saved.getTitle());
        return saved;
    }

    public ReviewResponse rateAndReviewContent(Integer watchedItemId, Integer rating, String review) {

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        WatchedItem item = watchedItemRepository.findById(watchedItemId)
                .orElseThrow(() -> new RuntimeException("Watched item not found"));

        boolean ratingChanged = !Objects.equals(item.getRating(), rating);
        boolean reviewChanged = !Objects.equals(item.getReview(), review);

        item.setRating(rating);
        item.setReview(review);

        if (ratingChanged || reviewChanged) {
            item.setReviewedAt(LocalDateTime.now());
        }

        item.setUpdatedAt(LocalDateTime.now());

        WatchedItem saved = watchedItemRepository.save(item);

        ContentMetadata metadata = getOrSaveContent(saved.getImdbId());

        ReviewResponse response = new ReviewResponse();
        response.setWatchedItemId(saved.getWatchedItemId());
        response.setImdbId(saved.getImdbId());
        response.setRating(saved.getRating());
        response.setReview(saved.getReview());
        response.setTitle(metadata.getTitle());
        response.setPlot(metadata.getPlot());

        return response;
    }

    private WatchedItem saveWatched(String imdbId) {
        return watchedItemRepository.findByImdbId(imdbId)
                .orElseGet(() -> {
                    WatchedItem watchedItem = new WatchedItem();

                    watchedItem.setImdbId(imdbId);
                    watchedItem.setWatchedAt(LocalDateTime.now());
                    watchedItem.setCreatedAt(LocalDateTime.now());
                    watchedItem.setUpdatedAt(LocalDateTime.now());

                    WatchedItem saved = watchedItemRepository.save(watchedItem);

                    log.info("Saved watched item for imdbId={}", imdbId);

                    return saved;
                });
    }

    @Transactional
    public List<WatchListItemDto> getWatchList() {
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

        List<WatchListItemDto> watchList = new ArrayList<>();

        for (WatchedItem item : watchedItems) {
            ContentMetadata metadata = metadataByImdbId.get(item.getImdbId());

            if (metadata == null) {
                metadata = getOrSaveContent(item.getImdbId());
            }
            // TODO: if metadata is missing:
            // 1. call OMDb API using imdbId
            // 2. save ContentMetadata to DB
            // 3. use fetched metadata instead of throwing

            watchList.add(new WatchListItemDto(
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
                    metadata.getMediaType(),
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

    private WatchListItemDto toWatchListItemDto(
            WatchedItem watchedItem,
            ContentMetadata metadata
    ) {
        return new WatchListItemDto(
                watchedItem.getWatchedItemId(),
                watchedItem.getImdbId(),
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
                metadata.getMetascore(),
                watchedItem.getRating(),
                watchedItem.getReview(),
                watchedItem.getWatchedAt(),
                watchedItem.getReviewedAt()
        );
    }
}

// TODO: Add user support so watchLists are scoped by userId instead of globally by imdbId.
// TODO: Add pagination for GET /watchList before the list gets large.
// TODO: Add filtering/sorting for watchList by rating, type, genre, watchedAt, reviewedAt.
// TODO: Add proper exception classes instead of RuntimeException.
// TODO: Add controller-level error handling with @ControllerAdvice.
// TODO: Add metadata refresh strategy for stale content_metadata records.
// TODO: Consider upsert behavior for content_metadata instead of plain save.
// TODO: Add tests for addToWatchList, rateAndReviewContent, and getWatchList.
// TODO: Add validation DTOs so controllers do not accept raw entity models directly.
// TODO: Consider Redis later as short-term cache in front of OMDb if traffic/API limits justify it.