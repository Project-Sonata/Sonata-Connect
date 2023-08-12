package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.repository.storage.RepositoryDelegatePlayerStateStorage;
import com.odeyalo.sonata.connect.repository.storage.support.PersistableEntityConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistingConfiguration {

    @Bean
    public PlayerStateStorage playerStateStorage(PlayerStateRepository<? extends PlayerState> repository,
                                                 PersistableEntityConverter<? extends PlayerState, PersistablePlayerState> converter) {
        return new RepositoryDelegatePlayerStateStorage(repository, converter);
    }
}
