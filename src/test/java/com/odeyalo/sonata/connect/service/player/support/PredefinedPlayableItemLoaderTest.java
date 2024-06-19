package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextUri;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayableItemFaker.TrackItemFaker;

import java.util.List;

class PredefinedPlayableItemLoaderTest {

    @Test
    void shouldReturnExistingItemByItsContextUri() {
        final var item = TrackItemFaker.create().get();

        final var testable = new PredefinedPlayableItemLoader(
                List.of(item)
        );

        testable.loadPlayableItem(item.getContextUri())
                .as(StepVerifier::create)
                .expectNext(item)
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfItemDoesNotExist() {
        final var testable = new PredefinedPlayableItemLoader();

        testable.loadPlayableItem(ContextUri.forTrack("123"))
                .as(StepVerifier::create)
                .verifyComplete();
    }
}