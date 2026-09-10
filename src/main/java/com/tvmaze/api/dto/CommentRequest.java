package com.tvmaze.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record CommentRequest(

        @NotNull(message = "show_id is required")
        @Positive(message = "show_id must be greater than 0")
        @JsonProperty("show_id")
        Long showId,

        @NotBlank(message = "comment is required")
        String comment,

        @NotNull(message = "rating is required")
        @Min(value = 0, message = "rating must be between 0 and 5")
        @Max(value = 5, message = "rating must be between 0 and 5")
        Integer rating
) {
}
