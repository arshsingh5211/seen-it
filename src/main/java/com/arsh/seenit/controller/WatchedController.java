package com.arsh.seenit.controller;

import com.arsh.seenit.dto.SearchDetailsDto;
import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.service.SearchService;
import com.arsh.seenit.service.WatchedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/watched")
@RequiredArgsConstructor
public class WatchedController {
    private final WatchedService watchedService;
    private final SearchService searchService;

    @GetMapping
    public Iterable<WatchedItem> findAll() {
        return watchedService.findAll();
    }

    @PostMapping
    public ResponseEntity<SearchDetailsDto> save(@RequestParam String imdbId) {
        WatchedItem watchedItem = watchedService.getWatchedItem(imdbId);
        watchedService.save(watchedItem);
        return ResponseEntity.status(201).body(searchService.getDetails(watchedItem.getImdbID()));
    }
}
