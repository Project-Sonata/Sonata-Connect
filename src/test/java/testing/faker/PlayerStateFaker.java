package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.*;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStateFaker {
    PlayerStateEntity.PlayerStateEntityBuilder builder = PlayerStateEntity.builder();

    final Faker faker = Faker.instance();

    public PlayerStateFaker() {
        this(-1);
    }

    public PlayerStateFaker(int numberOfDevices) {
        initializeWithFakedValues(numberOfDevices);
    }

    private void initializeWithFakedValues(int numberOfDevices) {
        builder.id(faker.random().nextLong(100000))
                .playing(faker.random().nextBoolean())
                .repeatState(faker.options().option(RepeatState.class))
                .shuffleState(faker.options().option(ShuffleMode.class))
                .progressMs(faker.random().nextLong(1000000))
                .playingType(faker.options().option(PlayingType.class))
                .volume(Volume.fromInt(faker.random().nextInt(0, 100)))
                .user(UserEntityFaker.create().get());

        if ( numberOfDevices <= 0 ) {
            builder.devicesEntity(DevicesEntityFaker.create().get());
        } else {
            builder.devicesEntity(DevicesEntityFaker.create(numberOfDevices).get());
        }
        builder.currentlyPlayingItem(TrackItemEntityFaker.create().get());
    }

    public static PlayerStateFaker create() {
        return new PlayerStateFaker();
    }

    public static PlayerStateFaker active() {
        TrackItemEntity item = TrackItemEntityFaker.create().get();
        return new PlayerStateFaker()
                .playing(true)
                .currentlyPlayingItem(item);
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

    public PlayerStateFaker shuffleState(ShuffleMode shuffleState) {
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

    public PlayerStateEntity get() {
        return builder.build();
    }

    public PlayerStateFaker paused() {
        return playing(false);
    }
}
