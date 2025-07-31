package com.playground.elasticsearch.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 검색 요청용 DTO
@Getter
@Setter
public class SearchDTO<T extends IndexDocument> {

    // 검색 용 param
    String searchText;

    String searchOption;

    List<T> documents;
}
