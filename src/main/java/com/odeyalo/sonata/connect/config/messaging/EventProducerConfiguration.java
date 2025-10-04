package com.odeyalo.sonata.connect.config.messaging;

import com.odeyalo.sonata.connect.service.messaging.MessageSendingTemplate;
import com.odeyalo.sonata.connect.service.messaging.SpyMessageSendingTemplate;
import com.odeyalo.sonata.connect.service.messaging.kafka.KafkaMessageSendingTemplate;
import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.kafka.sender.KafkaSender;

@Configuration
public class EventProducerConfiguration {

    @Bean
    @ConditionalOnBean(KafkaSender.class)
    @Profile("!test")
    public MessageSendingTemplate<String, SonataEvent> messageSendingTemplate(@NotNull final KafkaSender<String, SonataEvent> sender) {
        return new KafkaMessageSendingTemplate<>(sender);
    }

    @Bean
    @Profile("test")
    public SpyMessageSendingTemplate<String, SonataEvent> localMessageSendingTemplate() {
        return new SpyMessageSendingTemplate<>();
    }
}
