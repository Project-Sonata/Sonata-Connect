package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackItemEntity implements PlayableItemEntity {
    String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
