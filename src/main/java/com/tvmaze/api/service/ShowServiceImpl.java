package com.tvmaze.api.service;

import com.tvmaze.api.client.TvMazeClient;
import com.tvmaze.api.document.ShowCacheDocument;
import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.mapper.ShowMapper;
import com.tvmaze.api.repository.ShowCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowServiceImpl implements ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowMapper showMapper;
    private final ShowCacheRepository showCacheRepository;

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

    @Override
    public Map<String, Object> getShow(Long showId) {

        Optional<ShowCacheDocument> cachedShow =
                showCacheRepository.findById(showId);

        if (cachedShow.isPresent()) {
            log.info("Show {} retrieved from MongoDB cache", showId);
            return cachedShow.get().getPayload();
        }

        log.info("Show {} not found in MongoDB cache. Calling TVMaze API",
                showId);

        Map<String, Object> show = tvMazeClient.getShow(showId);

        showCacheRepository.save(
                ShowCacheDocument.builder()
                        .id(showId)
                        .payload(show)
                        .cachedAt(Instant.now())
                        .build()
        );

        log.info("Show {} cached successfully in MongoDB", showId);

        return show;
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