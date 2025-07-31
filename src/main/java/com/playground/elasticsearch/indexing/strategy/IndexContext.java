package com.playground.elasticsearch.indexing.strategy;

import java.util.HashMap;
import java.util.Map;

public class IndexContext {

    private Map<String, Object> options;

    public IndexContext() {
        this.options = new HashMap<>();
    }

    // index 색인 별 추가 옵션이 필요한 경우
    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
} 