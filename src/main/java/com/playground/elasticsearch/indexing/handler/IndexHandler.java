package com.playground.elasticsearch.indexing.handler;

import com.playground.elasticsearch.domain.dto.IndexDocument;
import com.playground.elasticsearch.indexing.strategy.IndexContext;
import com.playground.elasticsearch.indexing.strategy.IndexStrategy;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexHandler {
    private final Map<String, IndexStrategy<? extends IndexDocument>> strategies = new HashMap<>();

    public <T extends IndexDocument> void registerStrategy(Class<T> clazz, IndexStrategy<?> strategy) {
        // 클래스의 인덱스 이름을 키로 사용
        String indexName = getIndexNameFromStrategy(strategy);
        strategies.put(indexName, strategy);
    }

    private String getIndexNameFromStrategy(IndexStrategy<?> strategy) {
        // 전략의 타겟 클래스에서 인덱스 이름 추출
        Class<?> targetClass = strategy.getTargetIndex();
        try {
            IndexDocument sample = (IndexDocument) targetClass.getDeclaredConstructor().newInstance();
            return sample.getIndexName();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get index name from strategy", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IndexDocument> void handleIndex(List<T> documents, IndexContext context) {

        if(documents == null || documents.isEmpty()){
            // TODO: Exception 던지기
            return;
        }

        var document = documents.get(0);
        String indexName = document.getIndexName();

        IndexStrategy<T> strategy = (IndexStrategy<T>) strategies.get(indexName);

        if (strategy != null) {
            strategy.index(documents, context);
        } else {
            throw new IllegalArgumentException("No strategy registered for index: " + indexName);
        }

    }
}