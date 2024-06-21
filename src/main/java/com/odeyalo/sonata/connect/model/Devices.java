package com.odeyalo.sonata.connect.model;

import com.google.common.collect.Lists;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class Devices implements Iterable<Device> {
    @Getter(value = AccessLevel.PRIVATE)
    List<Device> devices;

    public boolean isEmpty() {
        return devices.isEmpty();
    }

    public int size() {
        return devices.size();
    }

    public Device get(int index) {
        return devices.get(index);
    }

    public Optional<Device> getActiveDevice() {
        return getDevices().stream().filter(Device::isActive).findFirst();
    }

    public boolean hasActiveDevice() {
        return getActiveDevice().isPresent();
    }

    public Stream<Device> stream() {
        return devices.stream();
    }

    @NotNull
    @Override
    public Iterator<Device> iterator() {
        return devices.iterator();
    }

    @NotNull
    public Devices connectDevice(@NotNull final Device device) {
        final DeviceSpec.DeviceStatus status = hasActiveDevice() ?
                DeviceSpec.DeviceStatus.IDLE :
                DeviceSpec.DeviceStatus.ACTIVE;

        final List<Device> devicesCopy = Lists.newArrayList(devices);

        devicesCopy.add(
                device.withActive(status.isActive())
        );

        return Devices.of(devicesCopy);
    }

    @NotNull
    public Devices disconnectDevice(@NotNull final TargetDeactivationDevice targetDevice) {
        final List<Device> newDevices = Lists.newArrayList(devices)
                .stream()
                .filter(device -> !Objects.equals(device.getId(), targetDevice.getDeviceId()))
                .toList();

        return Devices.of(newDevices);
    }
}
