package com.tvmaze.api.service;

import com.tvmaze.api.client.TvMazeClient;
import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.mapper.ShowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowServiceImpl implements ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowMapper showMapper;

    @Override
    public List<SearchShowResponse> search(String searchQuery) {

        log.info("Searching shows with query={}", searchQuery);

        List<SearchShowResponse> shows = tvMazeClient.searchShows(searchQuery).stream()
                .map(this::extractShow)
                .map(showMapper::toSearchResponse)
                .toList();

        log.info("Show search completed successfully. query={}, results={}",
                searchQuery, shows.size());

        return shows;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractShow(Map<String, Object> searchItem) {
        Object show = searchItem.get("show");

        if (!(show instanceof Map<?, ?>)) {
            return Map.of();
        }

        return (Map<String, Object>) show;
    }
}