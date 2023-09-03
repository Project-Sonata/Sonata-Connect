package testing.spring.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.socket.client.ReactorNetty2WebSocketClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

/**
 * Configuration for WebSocketClient that used for tests
 */
public class WebSocketClientTestConfiguration {
    /**
     * Create and configure the {@link org.springframework.web.reactive.socket.WebSocketHandler}.
     * Default implementation is {@link ReactorNetty2WebSocketClient} that is hardcoded.
     * @return - configured WebSocketClient
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}
