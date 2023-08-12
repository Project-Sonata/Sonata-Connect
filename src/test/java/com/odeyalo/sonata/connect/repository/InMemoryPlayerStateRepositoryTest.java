package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.InMemoryPlayerState;
import com.odeyalo.sonata.connect.entity.PlayerState;
import org.junit.jupiter.api.Test;
import testing.PlayerStatePersistentOperationsTestAdapter;
import testing.TestEntityGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPlayerStateRepositoryTest extends PlayerStatePersistentOperationsTestAdapter {
    static InMemoryPlayerStateRepository repository = new InMemoryPlayerStateRepository();

    InMemoryPlayerStateRepositoryTest() {
        super(repository, new InMemoryPlayerStateTestEntityGenerator());
    }

    @Test
    void expectInMemoryRepoType() {
        RepositoryType repositoryType = repository.getRepositoryType();

        assertThat(repositoryType).isEqualTo(RepositoryType.IN_MEMORY);
    }

    static class InMemoryPlayerStateTestEntityGenerator implements TestEntityGenerator<PlayerState> {

        @Override
        public PlayerState generateValidEntity() {
            return InMemoryPlayerState.builder()
                    .id(1L)
                    .build();
        }

        @Override
        public PlayerState generateInvalidEntity() {
            return InMemoryPlayerState.builder()
                    .id(-1L)
                    .build();
        }
    }
}
