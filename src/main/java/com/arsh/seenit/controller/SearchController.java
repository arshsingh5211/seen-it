package com.arsh.seenit.controller;

import com.arsh.seenit.dto.SearchDto;
import com.arsh.seenit.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public List<SearchDto> search(@RequestParam String query) {
        return searchService.search(query);
    }
}
