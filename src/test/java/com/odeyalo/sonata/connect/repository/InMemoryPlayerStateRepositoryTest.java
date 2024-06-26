package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import org.junit.jupiter.api.Test;
import testing.PlayerStatePersistentOperationsTestAdapter;
import testing.TestEntityGenerator;
import testing.faker.PlayerStateFaker;

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

    static class InMemoryPlayerStateTestEntityGenerator implements TestEntityGenerator<PlayerStateEntity> {

        @Override
        public PlayerStateEntity generateValidEntity() {
            return PlayerStateFaker.create()
                    .get();
        }

        @Override
        public PlayerStateEntity generateInvalidEntity() {
            return PlayerStateFaker.create()
                    .id(-1L)
                    .get();
        }
    }
}
