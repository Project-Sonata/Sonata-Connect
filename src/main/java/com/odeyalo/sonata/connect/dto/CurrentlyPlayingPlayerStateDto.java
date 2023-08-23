package com.odeyalo.sonata.connect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto to return current playing state, if any
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CurrentlyPlayingPlayerStateDto {
    private Boolean shuffleState;
}
