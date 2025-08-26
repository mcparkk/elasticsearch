package com.playground.elasticsearch.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContentDocument implements IndexDocument {

    String id;

    String title;

    List<String> tags;

    String describe;

    String indexName;

    @Override
    public String getIndexName() {
        return "content";
    }

}
