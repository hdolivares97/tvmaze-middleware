package com.tvmaze.api.service;

import com.tvmaze.api.dto.SearchShowResponse;

import java.util.List;
import java.util.Map;

public interface ShowService {

    /**
     * Searches shows matching the provided criteria.
     *
     * @param searchQuery search criteria
     * @return matching shows including their comments
     */
    List<SearchShowResponse> search(String searchQuery);

    /**
     * Retrieves a show by its identifier.
     *
     * @param showId show identifier
     * @return complete show information including comments
     */
    Map<String, Object> getShow(Long showId);
}