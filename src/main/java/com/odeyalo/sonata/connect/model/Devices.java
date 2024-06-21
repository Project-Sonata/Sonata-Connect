package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class Devices implements Iterable<Device> {
    @Getter(value = AccessLevel.PRIVATE)
    @Singular
    List<Device> devices;

    @NotNull
    public static Devices fromCollection(@NotNull Collection<Device> devices) {
        return builder().devices(devices).build();
    }

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

        return addDevice(
                device.withActive(status.isActive())
        );
    }

    @NotNull
    public Devices disconnectDevice(@NotNull final TargetDeactivationDevice targetDevice) {
        return removeDevice(targetDevice.getDeviceId());
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


    @NotNull
    private Device findDeviceToActivate(@NotNull final TargetDevice searchTarget) {
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
    private Devices removeDevice(@NotNull final String deviceId) {
        return getDevices().stream()
                .filter(device -> !Objects.equals(device.getId(), deviceId))
                .collect(CollectorImpl.instance());
    }

    @NotNull
    private Devices addDevice(@NotNull final Device device) {
        return Devices.builder()
                .devices(devices)
                .device(device)
                .build();
    }

    @NotNull
    private Devices getActiveDevices() {
        return devices.stream()
                .filter(Device::isActive)
                .collect(CollectorImpl.instance());
    }

    public static class CollectorImpl implements Collector<Device, DevicesBuilder, Devices> {

        public static CollectorImpl instance() {
            return new CollectorImpl();
        }

        @Override
        public Supplier<DevicesBuilder> supplier() {
            return Devices::builder;
        }

        @Override
        public BiConsumer<DevicesBuilder, Device> accumulator() {
            return DevicesBuilder::device;
        }

        @Override
        public BinaryOperator<DevicesBuilder> combiner() {
            return (b1, b2) -> b1.devices(b2.build().getDevices());
        }

        @Override
        public Function<DevicesBuilder, Devices> finisher() {
            return DevicesBuilder::build;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(
                    Characteristics.UNORDERED
            );
        }
    }
}
