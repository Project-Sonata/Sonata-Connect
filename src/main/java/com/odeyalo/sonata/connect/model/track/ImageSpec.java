package com.odeyalo.sonata.connect.model.track;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public interface ImageSpec {

    @NotNull
    URI getUrl();

    @Nullable
    Integer getHeight();

    @Nullable
    Integer getWidth();

}
