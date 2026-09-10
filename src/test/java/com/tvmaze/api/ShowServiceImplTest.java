package com.tvmaze.api;

import com.tvmaze.api.client.TvMazeClient;
import com.tvmaze.api.document.CommentDocument;
import com.tvmaze.api.document.ShowCacheDocument;
import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.mapper.ShowMapper;
import com.tvmaze.api.repository.CommentRepository;
import com.tvmaze.api.repository.ShowCacheRepository;
import com.tvmaze.api.service.ShowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ShowServiceImplTest {

    private TvMazeClient client;
    private ShowCacheRepository showRepository;
    private CommentRepository commentRepository;
    private ShowServiceImpl service;

    @BeforeEach
    void setUp() {
        client = mock(TvMazeClient.class);
        showRepository = mock(ShowCacheRepository.class);
        commentRepository = mock(CommentRepository.class);

        service = new ShowServiceImpl(
                client,
                new ShowMapper(),
                showRepository,
                commentRepository
        );
    }

    @Test
    void shouldReturnCachedShowWithoutCallingTvMaze() {
        Map<String, Object> payload = Map.of(
                "id", 1,
                "name", "Cached show"
        );

        when(showRepository.findById(1L))
                .thenReturn(Optional.of(
                        ShowCacheDocument.builder()
                                .id(1L)
                                .payload(payload)
                                .build()
                ));

        when(commentRepository.findByShowIdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of());

        Map<String, Object> result = service.getShow(1L);

        assertThat(result.get("name"))
                .isEqualTo("Cached show");

        assertThat(result.get("comments"))
                .isEqualTo(List.of());

        verify(showRepository).findById(1L);
        verify(commentRepository)
                .findByShowIdOrderByCreatedAtAsc(1L);

        verify(showRepository, never())
                .save(any(ShowCacheDocument.class));

        verifyNoInteractions(client);
    }

    @Test
    void shouldFetchAndCacheShowWhenNotCached() {
        Map<String, Object> payload = Map.of(
                "id", 2,
                "name", "Remote show"
        );

        when(showRepository.findById(2L))
                .thenReturn(Optional.empty());

        when(client.getShow(2L))
                .thenReturn(payload);

        when(commentRepository.findByShowIdOrderByCreatedAtAsc(2L))
                .thenReturn(List.of());

        Map<String, Object> result = service.getShow(2L);

        assertThat(result.get("name"))
                .isEqualTo("Remote show");

        assertThat(result.get("comments"))
                .isEqualTo(List.of());

        verify(client).getShow(2L);

        verify(showRepository).save(argThat(document ->
                document.getId().equals(2L)
                        && document.getPayload().equals(payload)
                        && document.getCachedAt() != null
        ));
    }

    @Test
    void shouldAddCommentsToSearchResponse() {
        Map<String, Object> show = Map.of(
                "id", 10,
                "name", "Example",
                "genres", List.of("Drama"),
                "network", Map.of("name", "ABC")
        );

        CommentDocument comment = CommentDocument.builder()
                .showId(10L)
                .comment("Great")
                .rating(5)
                .build();

        when(client.searchShows("example"))
                .thenReturn(List.of(
                        Map.of("show", show)
                ));

        when(commentRepository.findByShowIdOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(comment));

        List<SearchShowResponse> result =
                service.search("example");

        assertThat(result)
                .hasSize(1);

        SearchShowResponse response = result.get(0);

        assertThat(response.id())
                .isEqualTo(10L);

        assertThat(response.name())
                .isEqualTo("Example");

        assertThat(response.channel())
                .isEqualTo("ABC");

        assertThat(response.genres())
                .containsExactly("Drama");

        assertThat(response.comments())
                .hasSize(1);

        assertThat(response.comments().get(0).comment())
                .isEqualTo("Great");

        assertThat(response.comments().get(0).rating())
                .isEqualTo(5);
    }

    @Test
    void shouldUseWebChannelWhenNetworkIsNotAvailable() {
        Map<String, Object> show = Map.of(
                "id", 20,
                "name", "Streaming Show",
                "genres", List.of("Drama"),
                "webChannel", Map.of("name", "Netflix")
        );

        when(client.searchShows("streaming"))
                .thenReturn(List.of(
                        Map.of("show", show)
                ));

        when(commentRepository.findByShowIdOrderByCreatedAtAsc(20L))
                .thenReturn(List.of());

        List<SearchShowResponse> result =
                service.search("streaming");

        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).channel())
                .isEqualTo("Netflix");
    }
}
