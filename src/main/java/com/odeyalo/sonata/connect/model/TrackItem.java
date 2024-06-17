package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TrackItem implements TrackItemSpec {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    PlayableItemDuration duration;
    @NotNull
    ContextUri contextUri;
    boolean explicit;
    @NotNull
    Order order;

    public static TrackItem fromSpec(@NotNull final TrackItemSpec item) {
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
