package com.arsh.seenit.dto;

import lombok.Data;

@Data
public class ReviewResponse {
    private Integer watchedItemId;
    private String imdbId;
    private String title;
    private String plot;
    private String review;
    private Integer rating;

}
