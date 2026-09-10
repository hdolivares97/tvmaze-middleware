package com.tvmaze.api.repository;

import com.tvmaze.api.document.CommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<CommentDocument, String> {
    List<CommentDocument> findByShowIdOrderByCreatedAtAsc(Long showId);
}