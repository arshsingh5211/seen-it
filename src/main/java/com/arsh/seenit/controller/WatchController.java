package com.arsh.seenit.controller;

import com.arsh.seenit.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.WatchService;

@RequiredArgsConstructor
@RestController
public class WatchController {
    private final WatchService watchService;
    private final SearchService searchService;

    @GetMapping("/watched")
    public void getWatchedList() { // todo: param will eventually be a userid

    }
}
