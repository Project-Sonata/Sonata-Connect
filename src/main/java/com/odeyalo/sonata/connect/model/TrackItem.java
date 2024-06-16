package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TrackItem implements TrackItemSpec {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    PlayableItemDuration duration;
    @NotNull
    ContextUri contextUri;
    boolean explicit;
    @NotNull
    Order order;

    @Override
    @NotNull
    public PlayableItemType getItemType() {
        return PlayableItemType.TRACK;
    }
}
