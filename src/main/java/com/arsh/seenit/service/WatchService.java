package com.arsh.seenit.service;

import com.arsh.seenit.dto.WatchlistItemDto;
import com.arsh.seenit.model.ContentMetadata;
import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.repository.ContentMetadataRepository;
import com.arsh.seenit.repository.WatchedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WatchService {

    private final WatchedItemRepository watchedItemRepository;
    private final ContentMetadataRepository contentMetadataRepository;

    public WatchedItem update(Integer watchedItemId, Integer rating, String review) {
        WatchedItem item = watchedItemRepository.findById(watchedItemId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        item.setRating(rating);
        item.setReview(review);

        return watchedItemRepository.save(item);
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