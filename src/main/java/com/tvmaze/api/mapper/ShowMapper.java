package com.tvmaze.api.mapper;

import com.tvmaze.api.document.CommentDocument;
import com.tvmaze.api.dto.CommentResponse;
import com.tvmaze.api.dto.SearchShowResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ShowMapper {

    public SearchShowResponse toSearchResponse(Map<String, Object> show,
    List<CommentDocument> comments) {
        return new SearchShowResponse(
                toLong(show.get("id")),
                asString(show.get("name")),
                resolveChannel(show),
                asString(show.get("summary")),
                toStringList(show.get("genres")),
                toComments(comments)
        );
    }

    public List<CommentResponse> toComments(List<CommentDocument> comments) {
        return comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getComment(),
                        comment.getRating()
                ))
                .toList();
    }

    private String resolveChannel(Map<String, Object> show) {
        Object network = show.get("network");

        if (network instanceof Map<?, ?> networkMap
                && networkMap.get("name") != null) {
            return networkMap.get("name").toString();
        }

        Object webChannel = show.get("webChannel");

        if (webChannel instanceof Map<?, ?> webChannelMap
                && webChannelMap.get("name") != null) {
            return webChannelMap.get("name").toString();
        }

        return null;
    }

    private Long toLong(Object value) {
        return value instanceof Number number
                ? number.longValue()
                : Long.valueOf(value.toString());
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private List<String> toStringList(Object value) {
        if (!(value instanceof List<?> list)) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(String::valueOf)
                .toList();
    }
}
