package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.support.InMemory2PersistablePlayerStateConverter;
import testing.PlayerStatePersistentOperationsTestAdapter;
import testing.TestEntityGenerator;
import testing.faker.PlayerStateFaker;

class RepositoryDelegatePlayerStateStorageTest extends PlayerStatePersistentOperationsTestAdapter {

    public RepositoryDelegatePlayerStateStorageTest() {
        super(new RepositoryDelegatePlayerStateStorage(new InMemoryPlayerStateRepository(), new InMemory2PersistablePlayerStateConverter()), new PersistablePlayerStateTestEntityGenerator());
    }

    static class PersistablePlayerStateTestEntityGenerator implements TestEntityGenerator<PersistablePlayerState> {

        @Override
        public PersistablePlayerState generateValidEntity() {
            return PlayerStateFaker.create()
                    .asPersistablePlayerState();
        }

        @Override
        public PersistablePlayerState generateInvalidEntity() {
            return PlayerStateFaker.create()
                    .setId(-1L)
                    .asPersistablePlayerState();
        }
    }
}