package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.InMemoryPlayerState;
import com.odeyalo.sonata.connect.entity.InMemoryUserEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
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
                    .repeatState(RepeatState.OFF)
                    .shuffleState(false)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .user(InMemoryUserEntity.builder().id("mikuuu").build())
                    .build();
        }

        @Override
        public PlayerState generateInvalidEntity() {
            return InMemoryPlayerState.builder()
                    .id(-1L)
                    .repeatState(RepeatState.OFF)
                    .shuffleState(false)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .user(InMemoryUserEntity.builder().id("mikuuu").build())
                    .build();
        }
    }
}
