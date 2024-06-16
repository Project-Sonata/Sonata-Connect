package com.odeyalo.sonata.connect.model;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.odeyalo.sonata.connect.model.TrackItemSpec.Order;
import static org.assertj.core.api.Assertions.assertThat;

class TrackItemSpecOrderTest {

    @Test
    void shouldSortOrderFromFirstToLast() {
        final var orders = Lists.newArrayList(
                Order.of(0, 1),
                Order.of(0, 0),
                Order.of(0, 3),
                Order.of(0, 2)
        );

        Collections.sort(orders);

        assertThat(orders).isEqualTo(
                List.of(
                        Order.of(0, 0),
                        Order.of(0, 1),
                        Order.of(0, 2),
                        Order.of(0, 3)
                )
        );
    }

    @Test
    void shouldSortOrderFromFirstToLastConsideringDiscNumber() {
        final var orders = Lists.newArrayList(
                Order.of(1, 1),
                Order.of(1, 0),
                Order.of(0, 0),
                Order.of(1, 2)
        );

        Collections.sort(orders);

        assertThat(orders).isEqualTo(
                List.of(
                        Order.of(0, 0),
                        Order.of(1, 0),
                        Order.of(1, 1),
                        Order.of(1, 2)
                )
        );
    }
}