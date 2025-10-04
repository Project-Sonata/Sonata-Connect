package com.odeyalo.sonata.connect.config.kafka;

import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfiguration {

    @Bean
    public KafkaSender<String, SonataEvent> kafkaSender() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", JsonSerializer.class);

        SenderOptions<String, SonataEvent> senderOptions = SenderOptions.create(producerProps);

        return KafkaSender.create(senderOptions);
    }
}
