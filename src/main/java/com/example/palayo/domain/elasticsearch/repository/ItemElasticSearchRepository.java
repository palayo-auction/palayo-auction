package com.example.palayo.domain.elasticsearch.repository;

import com.example.palayo.domain.elasticsearch.document.ItemDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemElasticSearchRepository extends ElasticsearchRepository<ItemDocument, Long> {
    Page<ItemDocument> findBySellerId(Long sellerId, Pageable pageable);
}
