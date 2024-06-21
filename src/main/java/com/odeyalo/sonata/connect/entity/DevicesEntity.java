package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public List<DeviceEntity> getActiveDevices() {
        return items.stream().filter(DeviceEntity::isActive).toList();
    }

    public DeviceEntity get(int index) {
        return items.get(index);
    }

    public DeviceEntity getDevice(int index) {
        return get(index);
    }

    public void removeDevice(@NotNull final String deviceId) {
        getDevices().removeIf(device -> Objects.equals(device.getId(), deviceId));
    }

    public void removeIf(Predicate<DeviceEntity> predicate) {
        items.removeIf(predicate);
    }

    public boolean hasActiveDevice() {
        return items.stream().anyMatch(DeviceEntity::isActive);
    }

    public boolean hasNotActiveDevice() {
        return negate(hasActiveDevice());
    }

    public void activateDevice(@NotNull final DeviceEntity deviceToActivate) {
        removeDevice(deviceToActivate.getId());
        deviceToActivate.setActive(true);
        addDevice(deviceToActivate);
    }

    public void deactivateDevice(@NotNull final DeviceEntity deviceToDeactivate) {
        removeDevice(deviceToDeactivate.getId());
        deviceToDeactivate.setActive(false);
        addDevice(deviceToDeactivate);
    }

    public boolean containsById(String expectedId) {
        return getDevices().stream().anyMatch(device -> Objects.equals(device.getId(), expectedId));
    }

    public boolean hasDevice(final TargetDevice targetDevice) {
        return containsById(targetDevice.getId());
    }

    @NotNull
    public DevicesEntity transferPlayback(@NotNull final TargetDevice deviceToTransferPlayback) {

        final DeviceEntity currentlyActiveDevice = findCurrentlyActiveDevice();

        final DeviceEntity deviceToActivate = findDeviceToActivate(deviceToTransferPlayback);

        if ( currentlyActiveDevice != null ) {
            deactivateDevice(currentlyActiveDevice);
        }

        activateDevice(deviceToActivate);

        return this;
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

    @NotNull
    private DeviceEntity findDeviceToActivate(TargetDevice searchTarget) {
        return findById(searchTarget)
                .orElseThrow(() -> DeviceNotFoundException.withCustomMessage(String.format("Device with ID: %s not found", searchTarget.getId())));
    }

    @Nullable
    private DeviceEntity findCurrentlyActiveDevice() {
        return getActiveDevices().stream()
                .findFirst()
                .orElse(null);
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
