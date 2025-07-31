package com.playground.elasticsearch.domain.dto;

import lombok.Data;

@Data
public class SampleDocument implements IndexDocument{

    String id;
    String sampleData;

    @Override
    public String getIndexName() {
        return "sample";
    }
}
