package com.odeyalo.sonata.connect.service;

import com.odeyalo.sonata.connect.service.player.TargetDevice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Multiple device ids that are targeted by command
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TargetDevices implements Iterable<TargetDevice> {
    List<TargetDevice> devices;

    public static TargetDevices multiple(TargetDevice... devices) {
        return of(List.of(devices));
    }

    public static TargetDevices single(TargetDevice device) {
        return of(List.of(device));
    }

    public static TargetDevices empty() {
        return of(List.of());
    }

    public static TargetDevices fromDeviceIds(String[] deviceIds) {
        List<TargetDevice> targetDevices = Arrays.stream(deviceIds).map(TargetDevice::of).toList();
        return of(targetDevices);
    }

    public TargetDevice get(int index) {
        return devices.get(index);
    }

    public int size() {
        return devices.size();
    }

    public TargetDevice peekFirst() {
        return get(0);
    }

    public TargetDevice peekSecond() {
        return get(1);
    }

    public TargetDevice peekThird() {
        return get(2);
    }

    public boolean isEmpty() {
        return devices.isEmpty();
    }

    public boolean contains(Object o) {
        return devices.contains(o);
    }

    public boolean containsAll(@NotNull Collection<TargetDevice> c) {
        return new HashSet<>(devices).containsAll(c);
    }

    public Stream<TargetDevice> stream() {
        return devices.stream();
    }

    @NotNull
    @Override
    public Iterator<TargetDevice> iterator() {
        return devices.iterator();
    }

    public void forEach(Consumer<? super TargetDevice> action) {
        devices.forEach(action);
    }
}
