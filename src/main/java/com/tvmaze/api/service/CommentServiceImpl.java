package com.tvmaze.api.service;

import com.tvmaze.api.client.TvMazeClient;
import com.tvmaze.api.document.CommentDocument;
import com.tvmaze.api.dto.CommentRequest;
import com.tvmaze.api.dto.StatusResponse;
import com.tvmaze.api.repository.CommentRepository;
import com.tvmaze.api.repository.ShowCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ShowCacheRepository showCacheRepository;
    private final TvMazeClient tvMazeClient;

    @Override
    public StatusResponse addComment(CommentRequest request) {

        validateShowExists(request.showId());

        commentRepository.save(
                CommentDocument.builder()
                        .showId(request.showId())
                        .comment(request.comment())
                        .rating(request.rating())
                        .createdAt(Instant.now())
                        .build()
        );

        log.info("Comment added for showId={}. Comment: {}, Rating: {}",
                request.showId(), request.comment(), request.rating());

        return new StatusResponse("CREATED");
    }

    private void validateShowExists(Long showId) {
        if (!showCacheRepository.existsById(showId)) {
            tvMazeClient.getShow(showId);
        }
    }
}