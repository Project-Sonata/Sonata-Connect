package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.User;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStateFaker {
    PlayerState.PlayerStateBuilder builder = PlayerState.builder();

    final Faker faker = Faker.instance();

    public PlayerStateFaker() {
        this(-1);
    }

    public PlayerStateFaker(int numberOfDevices) {
        initializeWithFakedValues(numberOfDevices);
    }

    private void initializeWithFakedValues(int numberOfDevices) {
        builder.id((long) faker.random().nextInt(100000))
                .playing(faker.random().nextBoolean())
                .repeatState(faker.options().option(RepeatState.class))
                .shuffleState(faker.random().nextBoolean())
                .progressMs(faker.random().nextLong())
                .playingType(faker.options().option(PlayingType.class))
                .user(UserEntityFaker.create().get())
                .lastInteractionPlayPauseTime(Instant.now());

        if ( numberOfDevices <= 0 ) {
            builder.devicesEntity(DevicesEntityFaker.create().get());
        } else {
            builder.devicesEntity(DevicesEntityFaker.create(numberOfDevices).get());
        }
        builder.currentlyPlayingItem(TrackItemEntity.of(RandomStringUtils.randomAlphanumeric(16)));
    }

    public static PlayerStateFaker create() {
        return new PlayerStateFaker();
    }

    public static PlayerStateFaker forUser(User user) {
        PlayerStateFaker playerStateFaker = new PlayerStateFaker();
        UserEntity userEntity = UserEntity.builder().id(user.getId()).build();

        return playerStateFaker.user(userEntity);
    }

    public static PlayerStateFaker createWithCustomNumberOfDevices(int deviceNumber) {
        return new PlayerStateFaker(deviceNumber);
    }

    public PlayerStateFaker id(Long id) {
        builder.id(id);
        return this;
    }

    public PlayerStateFaker playing(boolean playing) {
        builder.playing(playing);
        return this;
    }

    public PlayerStateFaker repeatState(RepeatState repeatState) {
        builder.repeatState(repeatState);
        return this;
    }

    public PlayerStateFaker shuffleState(boolean shuffleState) {
        builder.shuffleState(shuffleState);
        return this;
    }

    public PlayerStateFaker progressMs(Long progressMs) {
        builder.progressMs(progressMs);
        return this;
    }

    public PlayerStateFaker playingType(PlayingType playingType) {
        builder.playingType(playingType);
        return this;
    }

    public PlayerStateFaker devicesEntity(DevicesEntity devicesEntity) {
        builder.devicesEntity(devicesEntity);
        return this;
    }

    public PlayerStateFaker user(UserEntity user) {
        builder.user(user);
        return this;
    }

    public PlayerStateFaker currentlyPlayingItem(PlayableItemEntity currentlyPlayingItem) {
        builder.currentlyPlayingItem(currentlyPlayingItem);
        return this;
    }

    public PlayerStateFaker device(DeviceEntity device) {
        DevicesEntity devices = DevicesEntity.builder().item(device).build();
        builder.devicesEntity(devices);
        return this;
    }

    public PlayerState get() {
        return builder.build();
    }

    public PlayerStateFaker paused() {
        return playing(false);
    }
}
