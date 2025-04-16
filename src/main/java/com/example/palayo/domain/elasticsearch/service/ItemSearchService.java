package com.example.palayo.domain.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.palayo.domain.elasticsearch.document.ItemDocument;
import com.example.palayo.domain.elasticsearch.dto.response.ItemSearchResponse;
import com.example.palayo.domain.elasticsearch.repository.ItemElasticSearchRepository;
import com.example.palayo.domain.item.enums.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemSearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final ItemElasticSearchRepository itemElasticSearchRepository;

    public Page<ItemSearchResponse> searchItems(String keyword, Category category, Long sellerId, Pageable pageable) {
        try {
            // 쿼리 동적 구성
            List<Query> mustQueries = new ArrayList<>();
            List<Query> filterQueries = new ArrayList<>();

            // keyword 검색 (name, content 둘 다)
            if (keyword != null && !keyword.isBlank()) {
                mustQueries.add(Query.of(q -> q
                        .multiMatch(m -> m
                                .fields("name", "content")
                                .query(keyword)
                        )
                ));
            }

            // sellerId는 필수
            filterQueries.add(Query.of(q -> q
                    .term(t -> t.field("sellerId").value(sellerId))
            ));

            // 카테고리는 선택
            if (category != null) {
                filterQueries.add(Query.of(q -> q
                        .term(t -> t.field("category").value(category.name()))
                ));
            }

            // 전체 Bool 쿼리 조합
            Query finalQuery = Query.of(q -> q
                    .bool(b -> b
                            .must(mustQueries)
                            .filter(filterQueries)
                    )
            );

            // 검색 실행
            SearchResponse<ItemDocument> response = elasticsearchClient.search(s -> s
                    .index("items")
                    .query(finalQuery)
                    .from((int) pageable.getOffset())
                    .size(pageable.getPageSize()), ItemDocument.class);

            // 결과 매핑
            List<ItemSearchResponse> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(ItemSearchResponse::of)
                    .toList();

            long totalHits = response.hits().total() != null ? response.hits().total().value() : 0;

            return new PageImpl<>(results, pageable, totalHits);

        } catch (IOException e) {
            log.error("엘라스틱서치 검색 실패", e);
            return Page.empty(pageable); // 예외 시 빈 페이지 반환
        }
    }

    public Page<ItemSearchResponse> findBySellerId(Long id, Pageable pageable) {
        Page<ItemDocument> items = itemElasticSearchRepository.findBySellerId(id, pageable);

        return items.map(ItemSearchResponse::of);
    }
}
