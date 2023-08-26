package com.odeyalo.sonata.connect.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TrackItem implements PlayableItem {
    String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public PlayableItemType getItemType() {
        return PlayableItemType.TRACK;
    }
}
