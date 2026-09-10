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

    /**
     * Searches shows by the provided search criteria.
     *
     * @param searchQuery search criteria
     * @return shows matching the search criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchShowResponse>> search(
            @RequestParam("search_query") @NotBlank String searchQuery) {
        return ResponseEntity.ok(showService.search(searchQuery));
    }

    /**
     * Retrieves a show by its identifier.
     *
     * @param showId show identifier
     * @return complete show information including comments
     */
    @GetMapping("/{showId}")
    public ResponseEntity<Map<String, Object>> getShow(@PathVariable @Positive Long showId) {
        return ResponseEntity.ok(showService.getShow(showId));
    }
}