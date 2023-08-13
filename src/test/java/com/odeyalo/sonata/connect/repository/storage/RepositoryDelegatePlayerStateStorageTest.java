package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.entity.InMemoryUserEntity;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.support.InMemory2PersistablePlayerStateConverter;
import testing.PlayerStatePersistentOperationsTestAdapter;
import testing.TestEntityGenerator;

class RepositoryDelegatePlayerStateStorageTest extends PlayerStatePersistentOperationsTestAdapter {

    public RepositoryDelegatePlayerStateStorageTest() {
        super(new RepositoryDelegatePlayerStateStorage(new InMemoryPlayerStateRepository(), new InMemory2PersistablePlayerStateConverter()), new PersistablePlayerStateTestEntityGenerator());
    }

    static class PersistablePlayerStateTestEntityGenerator implements TestEntityGenerator<PersistablePlayerState> {

        @Override
        public PersistablePlayerState generateValidEntity() {
            return PersistablePlayerState.builder()
                    .id(1L)
                    .repeatState(RepeatState.OFF)
                    .shuffleState(false)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .user(InMemoryUserEntity.builder().id("mikuuuu").build())
                    .build();
        }

        @Override
        public PersistablePlayerState generateInvalidEntity() {
            return PersistablePlayerState.builder()
                    .id(-1L)
                    .repeatState(RepeatState.OFF)
                    .shuffleState(false)
                    .playing(false)
                    .playingType(PlayingType.TRACK)
                    .user(InMemoryUserEntity.builder().id("mikuuuu").build())
                    .build();
        }
    }
}