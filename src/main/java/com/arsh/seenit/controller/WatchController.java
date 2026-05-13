package com.arsh.seenit.controller;

import com.arsh.seenit.dto.ReviewRequest;
import com.arsh.seenit.dto.WatchListItemDto;
import com.arsh.seenit.model.ContentMetadata;
import com.arsh.seenit.model.WatchedItem;
import com.arsh.seenit.service.WatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class WatchController {
    private final WatchService watchService;

    @GetMapping("/watched")
    public List<WatchListItemDto> getWatchList() { // todo: param will eventually be a userid
        return watchService.getWatchList();
    }

    @PostMapping("/watched/{imdbId}")
    public WatchedItem addToWatchList(@PathVariable String imdbId) {
        return watchService.addToWatchList(imdbId);
    }

    @DeleteMapping("/watched/{imdbId}")
    public void removeFromWatchList(@PathVariable String imdbId) {
        watchService.removeFromWatchList(imdbId);
    }

    @PostMapping("/watched/{watchedItemId}/review")
    public WatchedItem reviewWatched(
            @PathVariable Integer watchedItemId,
            @RequestBody ReviewRequest request
    ) {
        return watchService.rateAndReviewContent(
                watchedItemId,
                request.getRating(),
                request.getReview()
        );
    }
}
