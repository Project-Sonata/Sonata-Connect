package com.odeyalo.sonata.connect.entity.factory;

import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class DefaultPlayerStateEntityFactory implements PlayerStateEntityFactory {
    private final DeviceEntityFactory deviceFactory;
    private final PlayableItemEntityFactory playableItemFactory;

    public DefaultPlayerStateEntityFactory(final DeviceEntityFactory deviceFactory,
                                           final PlayableItemEntityFactory playableItemFactory) {
        this.deviceFactory = deviceFactory;
        this.playableItemFactory = playableItemFactory;
    }

    @Override
    @NotNull
    public PlayerStateEntity create(@NotNull final CurrentPlayerState state) {
        final List<DeviceEntity> devices = state.getDevices().stream().map(deviceFactory::create).toList();

        final PlayerStateEntity.PlayerStateEntityBuilder builder = PlayerStateEntity.builder()
                .id(state.getId())
                .playing(state.isPlaying())
                .volume(state.getVolume().asInt())
                .repeatState(state.getRepeatState())
                .shuffleState(state.getShuffleState())
                .user(UserEntity.builder().id(state.getUser().getId()).build())
                .devicesEntity(DevicesEntity.fromCollection(devices))
                .lastPauseTime(state.getLastPauseTime())
                .playStartTime(state.getPlayStartTime());


        if (state.getPlayableItem() != null) {
            final PlayableItemEntity playableItem = playableItemFactory.create(state.getPlayableItem());
            builder.currentlyPlayingItem(playableItem);
            builder.playingType(state.getPlayingType());
        }

        return builder.build();
    }
}
