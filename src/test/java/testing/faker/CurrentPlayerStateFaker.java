package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

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
                .devices(Devices.fromCollection(
                        Collections.singletonList(Device.of("123", "Odeyalo", DeviceType.COMPUTER,
                                DeviceSpec.Volume.from(
                                        faker.random().nextInt(0, 100)
                                )
                                , true))
                ))
                .progressMs(1000000L)
                .user(User.of(
                        RandomStringUtils.randomAlphanumeric(22)
                ))
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

    public CurrentPlayerStateFaker withUser(@NotNull User user) {
        builder.user(user);
        return this;
    }

    public CurrentPlayerState get() {
        return builder.build();
    }
}
