package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.model.Volume;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.SeekPosition;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.CurrentPlayerState2PlayerStateDtoConverter;
import com.odeyalo.sonata.connect.support.web.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public final class PlayerController {
    private final BasicPlayerOperations playerOperations;
    private final CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter;
    private final Converter<CurrentlyPlayingPlayerState, CurrentlyPlayingPlayerStateDto> currentlyPlayingPlayerStateDtoConverter;

    @GetMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PlayerStateDto> currentPlayerState(@NotNull final User user) {
        return playerOperations.currentState(user)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState2PlayerStateDtoConverter::convertTo);
    }

    @GetMapping(value = "/currently-playing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CurrentlyPlayingPlayerStateDto>> currentlyPlaying(@NotNull final User user) {
        return playerOperations.currentlyPlayingState(user)
                .subscribeOn(Schedulers.boundedElastic())
                .map(currentlyPlayingPlayerStateDtoConverter::convertTo)
                .map(HttpStatus::ok)
                .defaultIfEmpty(HttpStatus.default204Response());
    }

    @PutMapping(value = "/play", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> playOrResume(@NotNull final User user,
                                                @NotNull final PlayCommandContext commandContext) {
        return playerOperations.playOrResume(user, commandContext, null)
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(HttpStatus.default204Response());
    }

    @PutMapping(value = "/pause", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> pause(@NotNull final User user) {
        return playerOperations.pause(user)
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(HttpStatus.default204Response());
    }

    @PutMapping(value = "/shuffle", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> changeShuffleMode(@NotNull final User user,
                                                     @NotNull final ShuffleMode shuffleMode) {

        return playerOperations.changeShuffle(user, shuffleMode)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState -> HttpStatus.default204Response());
    }

    @PutMapping(value = "/volume", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> changePlayerVolume(@NotNull final Volume volume,
                                                      @NotNull final User user) {
        return playerOperations.changeVolume(user, volume)
                .subscribeOn(Schedulers.boundedElastic())
                .map(it -> HttpStatus.default204Response());
    }

    @PutMapping(value = "/seek", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> seekPlaybackPosition(@NotNull final User user,
                                                        @NotNull final SeekPosition seekPosition) {

        return playerOperations.seekToPosition(user, seekPosition)
                .thenReturn(HttpStatus.default204Response());
    }
}
