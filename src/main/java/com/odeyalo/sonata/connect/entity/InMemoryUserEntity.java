package com.odeyalo.sonata.connect.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserEntity implements UserEntity {
    String id;
}
