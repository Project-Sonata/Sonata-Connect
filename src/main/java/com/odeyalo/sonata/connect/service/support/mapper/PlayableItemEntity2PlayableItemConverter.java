package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.model.TrackItem;
import org.springframework.stereotype.Component;

/**
 * Convert PlayableItemEntity to PlayableItem
 */
@Component
public class PlayableItemEntity2PlayableItemConverter implements Converter<PlayableItemEntity, PlayableItem> {

    @Override
    public PlayableItem convertTo(PlayableItemEntity item) {
        if (item.getType() == PlayableItemType.TRACK) {
            return toTrackItem(item);
        }

        throw new UnsupportedOperationException(String.format("%s does not supported", item.getType()));
    }

    private static TrackItem toTrackItem(PlayableItemEntity item) {
        return TrackItem.of(item.getId());
    }
}