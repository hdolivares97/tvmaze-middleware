package com.tvmaze.api.controller;

import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.service.ShowService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Validated
public class ShowController {

    private final ShowService showService;

    @GetMapping("/search")
    public List<SearchShowResponse> search(
            @RequestParam(name = "search_query") String searchQuery) {

        return showService.search(searchQuery);
    }

    @GetMapping("/{showId}")
    public Map<String, Object> getShow(
            @PathVariable @Positive Long showId) {

        return showService.getShow(showId);
    }
}