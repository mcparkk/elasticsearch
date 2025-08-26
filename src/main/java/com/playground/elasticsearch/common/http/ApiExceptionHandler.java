package com.playground.elasticsearch.common.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request){
        
        log.info("###############################################");
        log.info("requestDispatcherPath : {}", request.getRequestURI());
        log.info("remoteAddr : {} ", request.getRemoteAddr());
        log.info("method : {}", request.getMethod());

        log.info("###############################################");
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
