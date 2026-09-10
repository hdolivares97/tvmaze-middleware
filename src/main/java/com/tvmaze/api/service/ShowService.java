package com.tvmaze.api.service;

import com.tvmaze.api.dto.SearchShowResponse;

import java.util.List;

public interface ShowService {

    List<SearchShowResponse> search(String searchQuery);
}