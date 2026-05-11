package com.arsh.seenit.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Data
public class WatchedItem {
    @Id
    private Integer watchedItemId;
    private String imdbId;
    private Integer rating;
    private String review;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime watchedAt;
    private LocalDateTime reviewedAt;
}