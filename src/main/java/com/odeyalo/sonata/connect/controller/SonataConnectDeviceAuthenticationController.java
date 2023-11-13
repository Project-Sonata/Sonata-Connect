package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.SonataConnectAuthenticationTokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/connect/auth")
public class SonataConnectDeviceAuthenticationController {

    @PostMapping
    public Mono<ResponseEntity<?>> generateSCAT() {

        return Mono.just(ResponseEntity.ok(
                SonataConnectAuthenticationTokenResponseDto.of("hello")
        ));
    }
}
