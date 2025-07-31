package com.playground.elasticsearch.indexing.strategy;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import com.playground.elasticsearch.domain.dto.ContentDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentIndexStrategy implements IndexStrategy<ContentDocument> {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Class<ContentDocument> getTargetIndex() {
        return ContentDocument.class;
    }

    @Override
    public void index(List<ContentDocument> documents, IndexContext context) {
        // 단건도 리스트로 변환하여 bulk 처리
        indexBulk(documents, context);
    }

    /**
     * ContentDocument 리스트를 대량으로 인덱싱
     */
    public void indexBulk(List<ContentDocument> documents, IndexContext context) {

        if (documents == null || documents.isEmpty()) {
            log.warn("Empty Data.");
            return;
        }

        try {
            log.info("ContentDocument bulk indexing start : count - {} ", documents.size());
            
            // 인덱스 이름 가져오기 (첫 번째 문서 기준)
            String indexName = documents.get(0).getIndexName();
            
            // BulkOperation 리스트 생성
            List<BulkOperation> operations = documents.stream()
                .map(document -> BulkOperation.of(b -> b
                    .index(IndexOperation.of(i -> i
                        .index(indexName)
                        .id(document.getId())
                        .document(document)
                    ))
                ))
                .collect(Collectors.toList());
            
            // BulkRequest 생성
            BulkRequest bulkRequest = BulkRequest.of(b -> b.operations(operations));
            
            // Elasticsearch에 대량 인덱싱 실행
            BulkResponse response = elasticsearchClient.bulk(bulkRequest);
            
            // 결과 확인
            if (response.errors()) {
                log.error("Bulk Indexing Error");
                response.items().forEach(item -> {
                    if (item.error() != null) {
                        log.error("Indexing Error - id: {}, msg: {}",
                            item.id(), item.error().reason());
                    }
                });
            } else {
                log.info("ContentDocument bulk indexing End: success count - {}", documents.size());
            }
            
            // 컨텍스트에서 추가 옵션 확인 (필요시)
            if (context != null && context.getOptions() != null) {
                log.debug("Context Option: {}", context.getOptions());
            }
            
        } catch (IOException e) {
            log.error("ContentDocument bulk indexing fail: count - {}, error={}",
                documents.size(), e.getMessage(), e);
            throw new RuntimeException("bulk indexing fail error msg: " + e.getMessage(), e);
        }
    }
} 