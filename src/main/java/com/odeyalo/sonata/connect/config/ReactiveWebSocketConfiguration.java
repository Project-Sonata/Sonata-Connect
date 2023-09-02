package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;

/**
 * Configuration for reactive web sockets
 */
@Configuration
public class ReactiveWebSocketConfiguration {
    @Autowired
    PlayerSynchronizationWebSocketHandler playerSynchronizationWebSocketHandler;

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> handlers = Map.of("/player/sync", playerSynchronizationWebSocketHandler);
        return new SimpleUrlHandlerMapping(handlers, -1);
    }
}