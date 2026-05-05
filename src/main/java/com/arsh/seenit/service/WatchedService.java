package com.arsh.seenit.service;

import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.repository.WatchedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchedService {

    private final WatchedItemRepository repository;

    public WatchedItem save(WatchedItem item) {
        return repository.save(item);
    }

    public Iterable<WatchedItem> findAll() {
        return repository.findAll();
    }

    public WatchedItem update(Integer id, Integer rating, String review) {
        WatchedItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setRating(rating);
        item.setReview(review);

        return repository.save(item);
    }
}