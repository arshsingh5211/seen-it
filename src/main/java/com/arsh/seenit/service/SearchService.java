package com.arsh.seenit.service;

import com.arsh.seenit.dto.SearchDetailsDto;
import com.arsh.seenit.dto.SearchDto;
import com.arsh.seenit.omdb.OmdbClient;
import com.arsh.seenit.omdb.OmdbMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
    private final OmdbClient omdbClient;
    private final OmdbMapper omdbMapper;

    public List<SearchDto> search(String query) {
        JsonNode response = omdbClient.search(query);
        return omdbMapper.toSearchResults(response);
    }

    public SearchDetailsDto getDetails(String id) {
        JsonNode response = omdbClient.getByImdbId(id);
        return omdbMapper.toSearchDetailsDto(response);
    }
}