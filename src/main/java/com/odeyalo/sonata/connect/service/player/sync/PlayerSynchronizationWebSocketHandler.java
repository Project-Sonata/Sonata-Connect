package com.odeyalo.sonata.connect.service.player.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.sonata.connect.dto.PlayerEventDto;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Synchronize devices using WebSocket API. Note that websockets in this class used as READ-ONLY and does not support write operation.
 * Write operations can be done using HTTP endpoints to control playback
 */
@Service
public class PlayerSynchronizationWebSocketHandler implements WebSocketHandler {
    private final PlayerSynchronizationManager playerSynchronizationManager;
    private final Converter<PlayerEvent, PlayerEventDto> playerState2PlayerStateDtoConverter;
    private final ObjectMapper objectMapper;

    @Autowired
    public PlayerSynchronizationWebSocketHandler(PlayerSynchronizationManager playerSynchronizationManager,
                                                 Converter<PlayerEvent, PlayerEventDto> playerState2PlayerStateDtoConverter,
                                                 ObjectMapper objectMapper) {
        this.playerSynchronizationManager = playerSynchronizationManager;
        this.playerState2PlayerStateDtoConverter = playerState2PlayerStateDtoConverter;
        this.objectMapper = objectMapper;
    }

    @NotNull
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> messages = resolveAuthentication()
                .flatMapMany(user -> receiveAndConvert(session, user));
        return session.send(messages);
    }

    @NotNull
    private Flux<WebSocketMessage> receiveAndConvert(WebSocketSession session, AuthenticatedUser user) {
        return playerSynchronizationManager.getEventStream(user)
                .mapNotNull(this::convertAndWriteAsJson)
                .map(session::textMessage);
    }

    private String convertAndWriteAsJson(PlayerEvent event) {
        PlayerEventDto dto = playerState2PlayerStateDtoConverter.convertTo(event);
        return dto != null ? json(dto) : null;
    }

    private <T> String json(T dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static Mono<AuthenticatedUser> resolveAuthentication() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .cast(AuthenticatedUser.class);
    }
}