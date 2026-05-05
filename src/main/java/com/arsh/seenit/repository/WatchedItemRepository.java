package com.arsh.seenit.repository;

import com.arsh.seenit.model.WatchedItem;
import org.springframework.data.repository.CrudRepository;

public interface WatchedItemRepository
        extends CrudRepository<WatchedItem, Integer> {
}
