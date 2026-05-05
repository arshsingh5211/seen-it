package com.arsh.seenit.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Data
public class WatchedItem {
    @Id
    private int watchedItemId;
    private String imdbID;
    private String title;
    private String releaseYear;
    private String mediaType;
    private String posterUrl;
    private Integer rating;
    private String review;
    private LocalDateTime createdAt;
}