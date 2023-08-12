package com.odeyalo.sonata.connect.model;

/**
 * An enumeration representing different repeat states for a media player.
 * You can use these states to control what should be repeated during playback.
 */
public enum RepeatState {
    /**
     * No repeat. Playback will continue normally without repeating anything.
     */
    OFF,

    /**
     * Repeat the currently playing track indefinitely.
     */
    TRACK,

    /**
     * Repeat the entire playback context, such as a playlist or album, indefinitely.
     */
    CONTEXT
}
