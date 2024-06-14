package com.odeyalo.sonata.connect.model;

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

    @Override
    @NotNull
    public PlayableItemType getItemType() {
        return PlayableItemType.TRACK;
    }
}
