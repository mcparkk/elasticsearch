package com.playground.elasticsearch.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class IndexDTO<T extends IndexDocument> {

    String metaDataSample;

    // IndexDocument 구현체
    List<T> documents;
}
