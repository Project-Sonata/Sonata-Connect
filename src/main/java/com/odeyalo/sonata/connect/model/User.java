package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 *  Model that represent User
 */
@AllArgsConstructor(staticName = "of")
@Builder
@Value
public class User {
    @NotNull
    String id;
}

