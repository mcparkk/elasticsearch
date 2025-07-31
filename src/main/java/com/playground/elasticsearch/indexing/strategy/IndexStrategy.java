package com.playground.elasticsearch.indexing.strategy;

import com.playground.elasticsearch.domain.dto.IndexDocument;

import java.util.List;

public interface IndexStrategy<T extends IndexDocument> {
    // 로직별 추가 파라미터
    void index(List<T> documents, IndexContext context);

    Class<T> getTargetIndex();
} 