package com.odeyalo.sonata.connect.entity.factory;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.DeviceSpec;
import org.jetbrains.annotations.NotNull;

public interface DeviceEntityFactory {

    @NotNull
    DeviceEntity create(@NotNull DeviceSpec spec);

    @NotNull
    DeviceEntity create(@NotNull DeviceSpec spec, @NotNull DeviceSpec.DeviceStatus status);


}
