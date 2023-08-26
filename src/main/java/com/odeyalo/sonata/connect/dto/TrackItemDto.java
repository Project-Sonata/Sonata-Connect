package com.odeyalo.sonata.connect.dto;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.model.PlayingType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackItemDto extends PlayableItemDto {

    public TrackItemDto(String id, String uri) {
        super(id, uri);
    }

    public static TrackItemDto of(String id) {
        return of(id, null);
    }

    public static TrackItemDto of(String id, String uri) {
        return new TrackItemDto(id, uri);
    }

    @Override
    public PlayableItemType getPlayingType() {
        return PlayableItemType.TRACK;
    }
}
