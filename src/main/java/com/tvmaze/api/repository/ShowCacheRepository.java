package com.tvmaze.api.repository;

import com.tvmaze.api.document.ShowCacheDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowCacheRepository extends MongoRepository<ShowCacheDocument, Long> {
}