package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.service.player.TargetDevice;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DevicesEntity implements Iterable<DeviceEntity> {
    @NotNull
    @Getter(value = AccessLevel.PRIVATE)
    private List<DeviceEntity> items;

    public static DevicesEntity empty() {
        return new DevicesEntity(new ArrayList<>());
    }

    public static DevicesEntity copyFrom(DevicesEntity parent) {
        return parent.toBuilder().build();
    }

    public static DevicesEntityBuilder builder() {
        return new DevicesEntityBuilder();
    }

    @NotNull
    public static DevicesEntity just(@NotNull final DeviceEntity... devices) {
        return builder().items(List.of(devices)).build();
    }

    @NotNull
    public static DevicesEntity fromCollection(@NotNull final Collection<DeviceEntity> devices) {
        Assert.noNullElements(devices, () -> "Null elements are not allowed!");
        return builder().items(devices).build();
    }

    @NotNull
    public List<DeviceEntity> getDevices() {
        return items;
    }

    public void addDevice(@NotNull final DeviceEntity device) {
        items.add(device);
    }

    public Stream<DeviceEntity> stream() {
        return items.stream();
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean contains(DeviceEntity o) {
        return items.contains(o);
    }

    public DeviceEntity get(int index) {
        return items.get(index);
    }

    public DeviceEntity getDevice(int index) {
        return get(index);
    }

    public boolean hasActiveDevice() {
        return items.stream().anyMatch(DeviceEntity::isActive);
    }

    @NotNull
    public Optional<DeviceEntity> findById(@NotNull final String expectedId) {
        return getDevices().stream()
                .filter(deviceEntity -> Objects.equals(deviceEntity.getId(), expectedId))
                .findFirst();
    }

    @NotNull
    public Optional<DeviceEntity> findById(@NotNull final TargetDevice searchTarget) {
        return findById(searchTarget.getId());
    }

    @NotNull
    @Override
    public Iterator<DeviceEntity> iterator() {
        return items.iterator();
    }

    public DevicesEntityBuilder toBuilder() {
        return new DevicesEntityBuilder().items(items);
    }

    public static class DevicesEntityBuilder {
        private ArrayList<DeviceEntity> items;

        public DevicesEntityBuilder item(DeviceEntity item) {
            if ( this.items == null ) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);
            return this;
        }

        public DevicesEntityBuilder items(Collection<? extends DeviceEntity> items) {
            if ( this.items == null ) {
                this.items = new ArrayList<>();
            }
            this.items.addAll(items);
            return this;
        }

        public DevicesEntityBuilder clearItems() {
            if ( this.items != null ) {
                this.items.clear();
            }
            return this;
        }

        public DevicesEntity build() {
            return new DevicesEntity(items);
        }

        public String toString() {
            return "DevicesEntity.DevicesEntityBuilder(items=" + this.items + ")";
        }
    }
}
