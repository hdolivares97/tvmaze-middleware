package com.tvmaze.api.controller;

import com.tvmaze.api.dto.SearchShowResponse;
import com.tvmaze.api.service.ShowService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SearchShowResponse>> search(
            @RequestParam("search_query") @NotBlank String searchQuery) {
        return ResponseEntity.ok(showService.search(searchQuery));
    }

    @GetMapping("/{showId}")
    public ResponseEntity<Map<String, Object>> getShow(@PathVariable @Positive Long showId) {
        return ResponseEntity.ok(showService.getShow(showId));
    }
}