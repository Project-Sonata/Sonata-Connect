package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.model.TrackItem;
import com.odeyalo.sonata.connect.model.TrackItemSpec;
import org.springframework.stereotype.Component;

/**
 * Convert {@link PlayableItemEntity} to {@link PlayableItem}
 */
@Component
public class PlayableItemEntity2PlayableItemConverter implements Converter<PlayableItemEntity, PlayableItem> {

    @Override
    public PlayableItem convertTo(PlayableItemEntity item) {
        if ( item == null ) {
            return null;
        }

        if ( item.getType() == PlayableItemType.TRACK ) {
            return toTrackItem((TrackItemSpec) item);
        }

        throw new UnsupportedOperationException(String.format("%s does not supported", item.getType()));
    }

    private static TrackItem toTrackItem(TrackItemSpec item) {
        return TrackItem.builder()
                .id(item.getId())
                .name(item.getName())
                .duration(item.getDuration())
                .contextUri(item.getContextUri())
                .explicit(item.isExplicit())
                .order(item.getOrder())
                .build();
    }
}