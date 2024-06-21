package com.odeyalo.sonata.connect.model;

import com.google.common.collect.Lists;
import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Value
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class Devices implements Iterable<Device> {
    @Getter(value = AccessLevel.PRIVATE)
    @Singular
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
    public Optional<Device> findById(@NotNull final String expectedId) {
        return getDevices().stream()
                .filter(deviceEntity -> Objects.equals(deviceEntity.getId(), expectedId))
                .findFirst();
    }

    @NotNull
    public Optional<Device> findById(@NotNull final TargetDevice searchTarget) {
        return findById(searchTarget.getId());
    }

    public boolean hasDevice(@NotNull final TargetDevice targetDevice) {
        return devices.stream().anyMatch(it -> Objects.equals(it.getId(), targetDevice.getId()));
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

    @NotNull
    public Devices transferPlayback(@NotNull final TargetDevice deviceToTransferPlayback) {

        final Device currentlyActiveDevice = findCurrentlyActiveDevice();

        final Device deviceToActivate = findDeviceToActivate(deviceToTransferPlayback);


        if ( currentlyActiveDevice != null ) {
            return deactivateDevice(currentlyActiveDevice)
                    .activateDevice(deviceToActivate);
        }

        return activateDevice(deviceToActivate);
    }

    @NotNull
    private Device findDeviceToActivate(TargetDevice searchTarget) {
        return findById(searchTarget)
                .orElseThrow(() -> DeviceNotFoundException.withCustomMessage(String.format("Device with ID: %s not found", searchTarget.getId())));
    }

    @Nullable
    private Device findCurrentlyActiveDevice() {
        return getActiveDevices().stream()
                .findFirst()
                .orElse(null);
    }

    @NotNull
    public Devices removeDevice(@NotNull final String deviceId) {
        final List<Device> updatedDevices = getDevices().stream()
                .filter(device -> !Objects.equals(device.getId(), deviceId))
                .toList();

        return Devices.of(updatedDevices);
    }

    @NotNull
    public Devices addDevice(@NotNull final Device device) {
        return Devices.builder()
                .devices(devices)
                .device(device)
                .build();
    }

    @NotNull
    public Devices activateDevice(@NotNull final Device deviceToActivate) {
        final Device updatedDevice = deviceToActivate.withActive(true);

        return removeDevice(deviceToActivate.getId())
                .addDevice(updatedDevice);
    }

    @NotNull
    public Devices deactivateDevice(@NotNull final Device deviceToDeactivate) {
        final Device deactivatedDevice = deviceToDeactivate.withActive(false);

        return removeDevice(deviceToDeactivate.getId())
                .addDevice(deactivatedDevice);
    }

    private Devices getActiveDevices() {

        final List<Device> activeDevices = devices.stream().filter(Device::isActive)
                .toList();

        return Devices.of(activeDevices);
    }
}
