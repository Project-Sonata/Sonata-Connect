package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.entity.factory.DeviceEntityFactory;
import com.odeyalo.sonata.connect.model.DeviceSpec;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceEntity {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    DeviceType deviceType;
    int volume;
    boolean active;

    public static DeviceEntity copy(DeviceEntity deviceEntity) {
        return deviceEntity.toBuilder().build();
    }

    public static final class Factory implements DeviceEntityFactory {

        @Override
        @NotNull
        public DeviceEntity create(@NotNull final DeviceSpec spec) {
            return create(spec, spec.getStatus());
        }

        @Override
        @NotNull
        public DeviceEntity create(@NotNull final DeviceSpec spec, @NotNull DeviceSpec.DeviceStatus status) {
            return builder()
                    .id(spec.getId())
                    .name(spec.getName())
                    .active(status.isActive())
                    .deviceType(spec.getType())
                    .volume(spec.getVolume().asInt())
                    .build();
        }
    }

}
