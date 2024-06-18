package com.odeyalo.sonata.connect.dto;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackItemDto extends PlayableItemDto {
    @NotNull
    String name;
    long durationMs;
    @NotNull
    String contextUri;
    boolean explicit;
    int discNumber;
    int index;
    @NotNull
    List<ArtistDto> artists;

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
