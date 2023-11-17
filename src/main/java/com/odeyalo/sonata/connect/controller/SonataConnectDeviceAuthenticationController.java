package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.SCATokenExchangeRequestDto;
import com.odeyalo.sonata.connect.dto.SCATokenExchangeResponseDto;
import com.odeyalo.sonata.connect.dto.SonataConnectAuthenticationTokenResponseDto;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.connect.DeviceConnectionAuthenticationTarget;
import com.odeyalo.sonata.connect.service.connect.SonataConnectManager;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/connect/auth")
public class SonataConnectDeviceAuthenticationController {

    private final SonataConnectManager sonataConnectManager;

    @Autowired
    public SonataConnectDeviceAuthenticationController(SonataConnectManager sonataConnectManager) {
        this.sonataConnectManager = sonataConnectManager;
    }

    @PostMapping
    public Mono<ResponseEntity<SonataConnectAuthenticationTokenResponseDto>> generateSCAT(@RequestParam("target_device_id") String targetDeviceId, AuthenticatedUser user) {

        return sonataConnectManager.generateSCAToken(DeviceConnectionAuthenticationTarget.of(targetDeviceId), User.of(user.getDetails().getId()))
                .map(scaToken -> SonataConnectAuthenticationTokenResponseDto.of(scaToken.getTokenValue().toString()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/exchange")
    public Mono<ResponseEntity<SCATokenExchangeResponseDto>> exchangeScatForAccessToken(@RequestBody SCATokenExchangeRequestDto body) {
        return sonataConnectManager.exchangeForToken(body.getScaToken())
                .map(accessToken -> SCATokenExchangeResponseDto.of(accessToken.getTokenValue()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
