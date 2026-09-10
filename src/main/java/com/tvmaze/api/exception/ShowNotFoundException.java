package com.tvmaze.api.exception;

public class ShowNotFoundException extends RuntimeException {
    public ShowNotFoundException(Long showId) {
        super("Show not found: " + showId);
    }
}