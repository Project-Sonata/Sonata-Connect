package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.entity.factory.PlayableItemEntityFactory;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.model.TrackItemSpec;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a track entity that can be played by a user
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackItemEntity implements PlayableItemEntity, TrackItemSpec {
    @NotNull
    String id;
    @NotNull
    String name;

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public PlayableItemType getType() {
        return getItemType();
    }

    public static final class Factory implements PlayableItemEntityFactory {

        @Override
        @NotNull
        public PlayableItemEntity create(@NotNull final PlayableItem item) {

            if ( !(item instanceof TrackItemSpec trackSpec) ) {
                throw new UnsupportedOperationException("Factory only supports type TRACK");
            }

            return TrackItemEntity.builder()
                    .id(trackSpec.getId())
                    .name(trackSpec.getName())
                    .build();
        }
    }
}
