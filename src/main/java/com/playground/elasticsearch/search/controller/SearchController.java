package com.playground.elasticsearch.search.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.playground.elasticsearch.common.http.ApiResponse;
import com.playground.elasticsearch.domain.dto.ContentDocument;
import com.playground.elasticsearch.domain.dto.SearchDTO;
import com.playground.elasticsearch.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/elastic/api/v1/search")
public class SearchController {

    private final ElasticsearchClient client;
    private final SearchService searchService;


    @PostMapping("/contents")
    public ApiResponse contents(@RequestBody SearchDTO<ContentDocument> param){

        var result = searchService.searchContent(param);

        return ApiResponse.ok(result);
    }

    @GetMapping("/test")
    public ApiResponse test(){

        Object result = null;

        try{
            SearchResponse<JsonData> response = client.search(s ->
                            s.index("page")
                                    .query(q -> q.matchAll(m -> m)),
                    JsonData.class
            );

            result = response.hits().hits().toString();
        }catch (Exception e){
            String melong = "melong";
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/test1")
    public ApiResponse test1(){

        Object result = null;

        try{
            SearchResponse<JsonData> response = client.search(s ->
                            s.index("kibana_sample_data_ecommerce")
                                    .query(q -> q.matchAll(m -> m)),
                    JsonData.class
            );

            result = response.hits().hits().toString();
        }catch (Exception e){
            String melong = "melong";
        }

        return ApiResponse.ok(result);
    }

    // TODO: 인덱스 전체 조회

    // TODO: 검색어 기반 조회


}
