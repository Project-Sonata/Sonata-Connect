package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor(staticName = "of")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonPlayableItemEntity implements PlayableItemEntity {
    String id;
    PlayableItemType itemType;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public PlayableItemType getType() {
        return itemType;
    }
}
