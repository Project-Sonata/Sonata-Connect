package com.odeyalo.sonata.connect.entity.factory;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import org.jetbrains.annotations.NotNull;

public interface PlayerStateEntityFactory {

    @NotNull
    PlayerStateEntity create(@NotNull CurrentPlayerState state);

}
