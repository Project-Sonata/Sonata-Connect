package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a track entity that can be player by a user
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackItemEntity implements PlayableItemEntity {
    @NotNull
    String id;

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
