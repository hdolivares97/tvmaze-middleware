package com.tvmaze.api.service;

import com.tvmaze.api.client.TvMazeClient;
import com.tvmaze.api.document.CommentDocument;
import com.tvmaze.api.document.ShowCacheDocument;
import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.mapper.ShowMapper;
import com.tvmaze.api.repository.CommentRepository;
import com.tvmaze.api.repository.ShowCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
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
    private final CommentRepository commentRepository;

    @Override
    public List<SearchShowResponse> search(String searchQuery) {

        log.info("Searching shows with query={}", searchQuery);

        List<SearchShowResponse> shows = tvMazeClient.searchShows(searchQuery)
                .stream()
                .map(this::extractShow)
                .map(show -> {
                    Long showId = ((Number) show.get("id")).longValue();

                    List<CommentDocument> comments =
                            commentRepository.findByShowIdOrderByCreatedAtAsc(showId);

                    return showMapper.toSearchResponse(show, comments);
                })
                .toList();

        log.info(
                "Show search completed successfully. query={}, results={}",
                searchQuery,
                shows.size()
        );

        return shows;
    }

    @Override
    public Map<String, Object> getShow(Long showId) {

        Optional<ShowCacheDocument> cachedShow =
                showCacheRepository.findById(showId);

        Map<String, Object> show;

        if (cachedShow.isPresent()) {
            log.info("Show {} retrieved from MongoDB cache", showId);
            show = cachedShow.get().getPayload();
        } else {
            log.info("Show {} not found in MongoDB cache. Calling TVMaze API",
                    showId);

            show = tvMazeClient.getShow(showId);

            showCacheRepository.save(
                    ShowCacheDocument.builder()
                            .id(showId)
                            .payload(show)
                            .cachedAt(Instant.now())
                            .build()
            );

            log.info("Show {} cached successfully in MongoDB", showId);
        }

        List<CommentDocument> comments =
                commentRepository.findByShowIdOrderByCreatedAtAsc(showId);

        Map<String, Object> response = new LinkedHashMap<>(show);

        response.put(
                "comments",
                showMapper.toComments(comments)
        );

        return response;
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