package com.odeyalo.sonata.connect.support.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class HttpStatus {


    @NotNull
    public static <T> ResponseEntity<T> default204Response() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @NotNull
    public static <T> ResponseEntity<T> ok(@NotNull final T body) {
        return ResponseEntity.ok(body);
    }
}
