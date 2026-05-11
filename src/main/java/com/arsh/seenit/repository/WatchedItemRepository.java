package com.arsh.seenit.repository;

import com.arsh.seenit.model.WatchedItem;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WatchedItemRepository extends CrudRepository<WatchedItem, Integer> {

    Optional<WatchedItem> findByImdbId(String imdbId);
}