package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
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

        if ( item instanceof TrackItemSpec trackSpec ) {
            return TrackItem.fromSpec(trackSpec);
        }

        throw new UnsupportedOperationException(String.format("%s does not supported", item.getType()));
    }
}