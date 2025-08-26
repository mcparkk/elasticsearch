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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/elastic/api/v1/search")
public class SearchController {

    private final ElasticsearchClient client;
    private final SearchService searchService;

    // 콘텐츠 text 조회
    @PostMapping("/contents")
    public ApiResponse contents(@RequestBody SearchDTO<ContentDocument> param){

        var result = searchService.searchContent(param);

        return ApiResponse.ok(result);
    }

    @PostMapping("/autoComplete")
    public ApiResponse autoComplete(@RequestBody SearchDTO<ContentDocument> param){



        return ApiResponse.ok(true);
    }

    // TODO: 인덱스 전체 조회

    // TODO: 검색어 기반 조회


}
