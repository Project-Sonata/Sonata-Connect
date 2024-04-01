package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.*;

import java.util.Collections;

public final class CurrentPlayerStateFaker {
    CurrentPlayerState.CurrentPlayerStateBuilder builder = CurrentPlayerState.builder();
    Faker faker = Faker.instance();

    public CurrentPlayerStateFaker() {
        PlayableItem playableItem = PlayableItemFaker.create().get();
        builder.playableItem(playableItem)
                .playingType(PlayingType.TRACK)
                .repeatState(faker.options().option(RepeatState.class))
                .id(faker.random().nextLong())
                .devices(Devices.of(
                        Collections.singletonList(Device.of("123", "Odeyalo", DeviceType.COMPUTER, 34, true))
                ))
                .progressMs(1000000L)
                .shuffleState(faker.random().nextBoolean());
    }

    public static CurrentPlayerStateFaker create() {
        return new CurrentPlayerStateFaker();
    }

    public CurrentPlayerStateFaker paused() {
        builder.playing(false);
        return this;
    }

    public CurrentPlayerStateFaker progressed() {
        builder.playing(true);
        return this;
    }

    public CurrentPlayerState get() {
        return builder.build();
    }
}
