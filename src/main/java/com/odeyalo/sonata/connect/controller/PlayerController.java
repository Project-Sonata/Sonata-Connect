package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.model.Volume;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.CurrentPlayerState2PlayerStateDtoConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
    private final BasicPlayerOperations playerOperations;
    private final CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter;
    private final Converter<CurrentlyPlayingPlayerState, CurrentlyPlayingPlayerStateDto> currentlyPlayingPlayerStateDtoConverter;

    @GetMapping("/state")
    public Mono<PlayerStateDto> currentPlayerState(User user) {

        return playerOperations.currentState(user)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState2PlayerStateDtoConverter::convertTo);
    }

    @GetMapping("/currently-playing")
    public Mono<ResponseEntity<CurrentlyPlayingPlayerStateDto>> currentlyPlaying(User user) {
        return playerOperations.currentlyPlayingState(user)
                .map(state -> ResponseEntity.ok(convertToCurrentlyPlayingStateDto(state)))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PutMapping("/play")
    public Mono<ResponseEntity<?>> playOrResume(User user, @RequestBody PlayResumePlaybackRequest body) {
        return playerOperations.playOrResume(user, PlayCommandContext.of(body.getContextUri()), null)
                .thenReturn(default204Response());
    }

    @PutMapping("/pause")
    public Mono<ResponseEntity<?>> pause(User user) {
        return playerOperations.pause(user).thenReturn(
                default204Response()
        );
    }

    @PutMapping("/shuffle")
    public Mono<ResponseEntity<?>> changeShuffleMode(@NotNull final User user,
                                                     @NotNull final ShuffleMode shuffleMode) {

        return playerOperations.changeShuffle(user, shuffleMode)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState -> default204Response());
    }

    @PutMapping(value = "/volume", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> changePlayerVolume(@NotNull final Volume volume,
                                                      @NotNull final User user) {
        return playerOperations.changeVolume(user, volume)
                .map(it -> default204Response());
    }

    @NotNull
    private CurrentlyPlayingPlayerStateDto convertToCurrentlyPlayingStateDto(CurrentlyPlayingPlayerState state) {
        return currentlyPlayingPlayerStateDtoConverter.convertTo(state);
    }

    @NotNull
    private static ResponseEntity<?> default204Response() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
