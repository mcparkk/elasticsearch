package com.playground.elasticsearch.common.http;

import org.springframework.http.HttpStatus;

public record ApiResponse(String code, String message, Object body) {

    public ApiResponse(Object body) {
        this(String.valueOf(HttpStatus.OK.value()), 
             HttpStatus.OK.getReasonPhrase(), 
             body);
    }

    public static ApiResponse ok(Object body) {
        return new ApiResponse(body);
    }
}
