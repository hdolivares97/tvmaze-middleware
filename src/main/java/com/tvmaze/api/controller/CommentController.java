package com.tvmaze.api.controller;

import com.tvmaze.api.dto.CommentRequest;
import com.tvmaze.api.dto.StatusResponse;
import com.tvmaze.api.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<StatusResponse> addComment(
            @Valid @RequestBody CommentRequest request) {

        StatusResponse response =
                commentService.addComment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}