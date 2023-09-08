package com.odeyalo.sonata.connect.service.player;


import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Immutable class to store multiple devices that should be deactivated
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TargetDeactivationDevices implements Iterable<TargetDeactivationDevice> {
    @Getter(value = AccessLevel.NONE)
    List<TargetDeactivationDevice> devices;

    public static TargetDeactivationDevices empty() {
        return of(emptyList());
    }

    public static TargetDeactivationDevices single(TargetDeactivationDevice device) {
        return of(singletonList(device));
    }

    public static TargetDeactivationDevices multiple(TargetDeactivationDevice... devices) {
        return of(List.of(devices));
    }

    public TargetDeactivationDevice get(int index) {
        return devices.get(index);
    }

    @NotNull
    @Override
    public Iterator<TargetDeactivationDevice> iterator() {
        return devices.iterator();
    }
}
