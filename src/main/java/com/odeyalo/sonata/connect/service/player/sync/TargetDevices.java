package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.service.player.TargetDevice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    public TargetDevice get(int index) {
        return devices.get(index);
    }

    public int size() {
        return devices.size();
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
