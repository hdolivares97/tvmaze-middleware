package com.tvmaze.api.service;

import com.tvmaze.api.dto.CommentRequest;
import com.tvmaze.api.dto.StatusResponse;

public interface CommentService {

    /**
     * Adds a comment and rating to an existing show.
     *
     * @param request comment and rating information
     */
    StatusResponse addComment(CommentRequest request);
}