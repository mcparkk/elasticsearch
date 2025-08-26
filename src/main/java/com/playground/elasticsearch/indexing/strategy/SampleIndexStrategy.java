package com.playground.elasticsearch.indexing.strategy;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.playground.elasticsearch.domain.dto.SampleDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleIndexStrategy implements IndexStrategy<SampleDocument> {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void index(List<SampleDocument> documents, IndexContext context) {

    }

    @Override
    public Class<SampleDocument> getTargetIndex() {
        return SampleDocument.class;
    }

}
