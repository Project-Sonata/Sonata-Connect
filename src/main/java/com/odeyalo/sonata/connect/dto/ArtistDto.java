package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
public class ArtistDto {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    String contextUri;
}
