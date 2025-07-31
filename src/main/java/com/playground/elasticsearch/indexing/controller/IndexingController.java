package com.playground.elasticsearch.indexing.controller;

import com.playground.elasticsearch.common.http.ApiResponse;
import com.playground.elasticsearch.domain.dto.ContentDocument;
import com.playground.elasticsearch.domain.dto.IndexDTO;
import com.playground.elasticsearch.indexing.service.IndexingService;
import com.playground.elasticsearch.indexing.strategy.IndexContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/elastic/api/v1/indexing")
public class IndexingController {

    private final IndexingService indexingService;

    // content 인덱스 indexing
    @PostMapping("/contents")
    public ApiResponse indexContent(@RequestBody IndexDTO<ContentDocument> dto) {
        // 예시: 옵션 등 추가 가능
        IndexContext context = new IndexContext();

        indexingService.indexContent(dto.getDocuments(), context);
        return ApiResponse.ok(true);
    }
}
