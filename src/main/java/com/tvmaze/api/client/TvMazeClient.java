package com.tvmaze.api.client;

import com.tvmaze.api.exception.ExternalServiceException;
import com.tvmaze.api.exception.ShowNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TvMazeClient {

    private final RestClient tvMazeRestClient;

    /**
     * Searches shows in TVMaze using the provided query.
     */
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

    /**
     * Retrieves a show by ID from TVMaze.
     *
     * @throws ShowNotFoundException if the show does not exist
     * @throws ExternalServiceException if TVMaze cannot process the request
     */
    public Map<String, Object> getShow(Long showId) {
        try {
            Map<String, Object> response = tvMazeRestClient.get()
                    .uri("/shows/{id}", showId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            (request, responseError) -> {
                                throw new ShowNotFoundException(showId);
                            }
                    )
                    .onStatus(
                            HttpStatusCode::isError,
                            (request, responseError) -> {
                                throw new ExternalServiceException(
                                        "TVMaze returned HTTP "
                                                + responseError.getStatusCode()
                                );
                            }
                    )
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null) {
                throw new ExternalServiceException(
                        "TVMaze returned an empty response"
                );
            }

            return response;

        } catch (ShowNotFoundException | ExternalServiceException ex) {
            throw ex;

        } catch (RestClientException ex) {
            throw new ExternalServiceException(
                    "Unable to obtain show from TVMaze",
                    ex
            );
        }
    }
}
