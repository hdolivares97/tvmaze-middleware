package com.tvmaze.api.client;

import com.tvmaze.api.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TvMazeClient {

    private final RestClient tvMazeRestClient;

    public List<Map<String, Object>> searchShows(String query) {
        try {
            List<Map<String, Object>> response = tvMazeRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/shows")
                            .queryParam("q", query)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            return response == null ? List.of() : response;

        } catch (RestClientException ex) {
            throw new ExternalServiceException(
                    "Unable to search shows in TVMaze",
                    ex
            );
        }
    }
}
