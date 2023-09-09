package com.odeyalo.sonata.connect;

import com.odeyalo.sonata.connect.entity.InMemoryDeviceEntity;
import com.odeyalo.sonata.connect.entity.InMemoryDevicesEntity;
import com.odeyalo.sonata.connect.entity.InMemoryUserEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SonataConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonataConnectApplication.class, args);
    }


    @Bean
    public ApplicationRunner runner(PlayerStateStorage playerStateStorage) {
        return args -> {
            InMemoryDevicesEntity devices = InMemoryDevicesEntity.builder()
                    .device(InMemoryDeviceEntity.builder()
                            .id("something")
                            .name("Miku")
                            .deviceType(DeviceType.COMPUTER)
                            .volume(50)
                            .active(true)
                            .build())
                    .build();
            PersistablePlayerState playerState = PersistablePlayerState.builder()
                    .id(1L)
                    .shuffleState(PlayerState.SHUFFLE_DISABLED)
                    .progressMs(0L)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .repeatState(RepeatState.OFF)
                    .user(InMemoryUserEntity.builder().id("mikku").build())
                    .devicesEntity(devices)
                    .build();
            playerStateStorage.save(playerState).block();
        };
    }
}
