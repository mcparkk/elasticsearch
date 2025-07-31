package com.playground.elasticsearch.indexing.service;

import com.playground.elasticsearch.domain.dto.ContentDocument;
import com.playground.elasticsearch.domain.dto.IndexDocument;
import com.playground.elasticsearch.indexing.handler.IndexHandler;
import com.playground.elasticsearch.indexing.strategy.IndexContext;
import com.playground.elasticsearch.indexing.strategy.IndexStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexingService {

    private final IndexHandler indexHandler = new IndexHandler();
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        // IndexStrategy 구현체들을 모두 가져와서 등록
        Map<String, IndexStrategy> strategyBeans = applicationContext.getBeansOfType(IndexStrategy.class);
        
        for (IndexStrategy strategy : strategyBeans.values()) {
            indexHandler.registerStrategy(strategy.getTargetIndex(), strategy);
        }
        
        log.info("Strategy Count: " + strategyBeans.size());
    }

    public <T extends IndexDocument> void indexContent(List<T> documents, IndexContext context) {
        indexHandler.handleIndex(documents, context);
    }
}














