package com.odeyalo.sonata.connect.service.messaging.kafka;

import com.odeyalo.sonata.connect.service.messaging.MessageSendingTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

public final class KafkaMessageSendingTemplate<K, V> implements MessageSendingTemplate<K, V> {
    private final KafkaSender<K, V> kafkaSender;

    public KafkaMessageSendingTemplate(final KafkaSender<K, V> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @Override
    @NotNull
    public Mono<Void> send(@NotNull final Publisher<Record<K, V>> events) {

        final Flux<SenderRecord<K, V, Object>> senderRecords = Flux.from(events).map(
                (record) -> SenderRecord.create(new ProducerRecord<>(
                                record.getTopic(),
                                record.getKey(),
                                record.getValue()
                        ), null
                ));

        return kafkaSender.send(senderRecords)
                .then();
    }
}
