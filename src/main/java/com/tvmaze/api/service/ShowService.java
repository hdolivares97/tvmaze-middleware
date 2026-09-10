package com.tvmaze.api.service;

import com.tvmaze.api.dto.SearchShowResponse;

import java.util.List;
import java.util.Map;

public interface ShowService {

    List<SearchShowResponse> search(String searchQuery);

    Map<String, Object> getShow(Long showId);
}