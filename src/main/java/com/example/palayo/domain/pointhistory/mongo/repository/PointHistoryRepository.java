package com.example.palayo.domain.pointhistory.mongo.repository;

import com.example.palayo.domain.pointhistory.mongo.document.PointHistoryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PointHistoryRepository extends MongoRepository<PointHistoryDocument, String> {
    Page<PointHistoryDocument> findByUserId(Long userId, Pageable pageable);
}
