package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for InMemoryRoomHolder
 */
class InMemoryRoomHolderTest {

    @Test
    void getOrCreateRoom() {
        InMemoryRoomHolder repository = new InMemoryRoomHolder();
        User user = createUser();
        Room room = repository.getOrCreateRoom(user).block();

        assertThat(room).isNotNull();
    }

    @Test
    void mustReuseExistingOne() {
        InMemoryRoomHolder repository = new InMemoryRoomHolder();
        User user = createUser();
        Room first = repository.getOrCreateRoom(user).block();

        assertNotNull(first);

        Room second = repository.getOrCreateRoom(user).block();

        assertThat(first).isEqualTo(second);
    }

    @NotNull
    private static User createUser() {
        return User.of("123");
    }

    @Test
    void shouldThrowException() {
        InMemoryRoomHolder repository = new InMemoryRoomHolder();
        assertThatThrownBy(() -> repository.getOrCreateRoom(null).block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User cannot be null!");

    }
}