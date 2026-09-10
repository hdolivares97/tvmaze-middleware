package com.tvmaze.api.service;

import com.tvmaze.api.dto.CommentRequest;
import com.tvmaze.api.dto.StatusResponse;

public interface CommentService {

    StatusResponse addComment(CommentRequest request);
}