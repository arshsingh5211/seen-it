package com.arsh.seenit.controller;

import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.service.WatchedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/watched")
@RequiredArgsConstructor
public class WatchedController {
    private final WatchedService watchedService;

    @GetMapping
    public Iterable<WatchedItem> findAll() {
        return watchedService.findAll();
    }

    @PostMapping
    public ResponseEntity<WatchedItem> save(@RequestBody WatchedItem item) {
        WatchedItem saved = watchedService.save(item);
        return ResponseEntity.status(201).body(saved);
    }
}
