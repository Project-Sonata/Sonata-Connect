package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 *  Model that represent User
 */
@AllArgsConstructor(staticName = "of")
@Builder
@Value
public class User {
    String id;
}

