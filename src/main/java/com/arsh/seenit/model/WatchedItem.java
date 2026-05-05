package com.arsh.seenit.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Data
public class WatchedItem {
    @Id
    private int id;
    private String imdbID;
    private String title;
    private String year;
    private String type;
    private String poster;
    private Integer rating;
    private String review;
    private LocalDateTime createdAt;
}