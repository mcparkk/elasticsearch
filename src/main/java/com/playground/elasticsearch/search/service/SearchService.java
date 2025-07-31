package com.playground.elasticsearch.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.playground.elasticsearch.domain.dto.ContentDocument;
import com.playground.elasticsearch.domain.dto.SearchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchClient client;

    // content document searching
    public SearchDTO<ContentDocument> searchContent(SearchDTO<ContentDocument> dto){

        try{

            String indexName = new ContentDocument().getIndexName();

//            SearchResponse<ContentDocument> response = client.search(s -> s
//                            .index(indexName)
//                            .query(q -> q
//                                    .matchAll(m -> m)),
//                            ContentDocument.class
//            );

            SearchResponse<ContentDocument> response = client.search(s -> s
                            .index(indexName)
                            .query(q -> q.match(m -> m.field("combined")
                                                    .query(dto.getSearchText()))),
                            ContentDocument.class
            );

            var list = response.hits().hits().stream().map(Hit::source).toList();

            dto.setDocuments(list);

        }catch (Exception e){
            String melong = "melong";
        }

        return dto;
    }

}
