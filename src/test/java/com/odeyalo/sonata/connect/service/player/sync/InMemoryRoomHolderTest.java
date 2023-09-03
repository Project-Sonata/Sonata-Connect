package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.suite.security.auth.AuthenticatedUser;
import com.odeyalo.suite.security.auth.AuthenticatedUserDetails;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static java.util.Set.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InMemoryRoomHolder
 */
class InMemoryRoomHolderTest {

    @Test
    void getOrCreateRoom() {
        InMemoryRoomHolder repository = new InMemoryRoomHolder();
        AuthenticatedUser user = createAuthenticatedUser();
        Room room = repository.getOrCreateRoom(user).block();

        assertThat(room).isNotNull();
    }

    @Test
    void mustReuseExistingOne() {
        InMemoryRoomHolder repository = new InMemoryRoomHolder();
        AuthenticatedUser user = createAuthenticatedUser();
        Room first = repository.getOrCreateRoom(user).block();

        assertNotNull(first);

        Room second = repository.getOrCreateRoom(user).block();

        assertThat(first).isEqualTo(second);
    }

    @NotNull
    private static AuthenticatedUser createAuthenticatedUser() {
        Set<GrantedAuthority> authorities = of(new SimpleGrantedAuthority("read"), new SimpleGrantedAuthority("write"));
        AuthenticatedUserDetails details = new AuthenticatedUserDetails("id", "odeyalo", "password", authorities);
        AuthenticatedUser user = AuthenticatedUser.of(details, "odeyalo", authorities);
        return user;
    }

    @Test
    void shouldThrowException() {
        InMemoryRoomHolder repository = new InMemoryRoomHolder();
        assertThatThrownBy(() -> repository.getOrCreateRoom(null).block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User cannot be null!");

    }
}