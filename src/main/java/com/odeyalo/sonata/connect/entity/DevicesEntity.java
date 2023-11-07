package com.odeyalo.sonata.connect.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.apache.commons.lang3.BooleanUtils.negate;

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

    public List<DeviceEntity> getDevices() {
        return items;
    }

    public void addDevice(DeviceEntity device) {
        Assert.notNull(device, "Device should be not null!");
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

    public void removeIf(Predicate<DeviceEntity> predicate) {
        items.removeIf(predicate);
    }

    public List<DeviceEntity> getActiveDevices() {
        return items.stream().filter(DeviceEntity::isActive).toList();
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

    public boolean hasNotActiveDevice() {
        return negate(hasActiveDevice());
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
