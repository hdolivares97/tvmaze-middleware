package com.tvmaze.api.controller;

import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping("/search")
    public List<SearchShowResponse> search(
            @RequestParam(name = "search_query") String searchQuery) {

        return showService.search(searchQuery);
    }
}