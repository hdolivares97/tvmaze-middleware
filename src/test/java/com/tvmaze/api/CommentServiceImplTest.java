package com.tvmaze.api;

import com.tvmaze.api.client.TvMazeClient;
import com.tvmaze.api.document.CommentDocument;
import com.tvmaze.api.dto.CommentRequest;
import com.tvmaze.api.dto.StatusResponse;
import com.tvmaze.api.exception.ShowNotFoundException;
import com.tvmaze.api.repository.CommentRepository;
import com.tvmaze.api.repository.ShowCacheRepository;
import com.tvmaze.api.service.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    private CommentRepository commentRepository;
    private ShowCacheRepository showCacheRepository;
    private TvMazeClient tvMazeClient;
    private CommentServiceImpl service;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        showCacheRepository = mock(ShowCacheRepository.class);
        tvMazeClient = mock(TvMazeClient.class);

        service = new CommentServiceImpl(
                commentRepository,
                showCacheRepository,
                tvMazeClient
        );
    }

    @Test
    void shouldSaveCommentWhenShowExistsInCache() {
        CommentRequest request = new CommentRequest(
                1L,
                "Great show",
                5
        );

        when(showCacheRepository.existsById(1L))
                .thenReturn(true);

        StatusResponse response =
                service.addComment(request);

        assertThat(response.status())
                .isEqualTo("CREATED");

        verify(showCacheRepository)
                .existsById(1L);

        verify(commentRepository).save(argThat(comment ->
                comment.getShowId().equals(1L)
                        && comment.getComment().equals("Great show")
                        && comment.getRating().equals(5)
                        && comment.getCreatedAt() != null
        ));

        verifyNoInteractions(tvMazeClient);
    }

    @Test
    void shouldValidateShowInTvMazeWhenNotCached() {
        CommentRequest request = new CommentRequest(
                2L,
                "Good show",
                4
        );

        when(showCacheRepository.existsById(2L))
                .thenReturn(false);

        when(tvMazeClient.getShow(2L))
                .thenReturn(Map.of(
                        "id", 2,
                        "name", "Remote show"
                ));

        StatusResponse response =
                service.addComment(request);

        assertThat(response.status())
                .isEqualTo("CREATED");

        verify(tvMazeClient)
                .getShow(2L);

        verify(commentRepository).save(argThat(comment ->
                comment.getShowId().equals(2L)
                        && comment.getComment().equals("Good show")
                        && comment.getRating().equals(4)
                        && comment.getCreatedAt() != null
        ));
    }

    @Test
    void shouldNotSaveCommentWhenShowDoesNotExist() {
        CommentRequest request = new CommentRequest(
                999L,
                "Invalid show",
                3
        );

        when(showCacheRepository.existsById(999L))
                .thenReturn(false);

        when(tvMazeClient.getShow(999L))
                .thenThrow(new ShowNotFoundException(999L));

        assertThatThrownBy(() ->
                service.addComment(request)
        )
                .isInstanceOf(ShowNotFoundException.class);

        verify(tvMazeClient)
                .getShow(999L);

        verify(commentRepository, never())
                .save(any(CommentDocument.class));
    }
}