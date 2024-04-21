package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * Represent any item that can be played by a user
 */
@Data
@AllArgsConstructor(staticName = "of")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonPlayableItemEntity implements PlayableItemEntity {
    @NotNull
    String id;
    @NotNull
    PlayableItemType itemType;

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public PlayableItemType getType() {
        return itemType;
    }
}
