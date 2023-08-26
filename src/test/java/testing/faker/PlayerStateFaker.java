package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.TrackItem;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.function.Function;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStateFaker {
    Long id;
    boolean playing;
    RepeatState repeatState;
    boolean shuffleState;
    Long progressMs;
    PlayingType playingType;
    Devices devices;
    UserEntity user;
    PlayableItemEntity playingItem;

    final Faker faker = Faker.instance();

    public PlayerStateFaker() {
        this(-1);
    }

    public PlayerStateFaker(int numberOfDevices) {
        initializeWithFakedValues(numberOfDevices);
    }

    private void initializeWithFakedValues(int numberOfDevices) {
        this.id = Long.valueOf(faker.random().nextInt(0, 100000));
        this.playing = faker.random().nextBoolean();
        this.repeatState = faker.options().option(RepeatState.class);
        this.shuffleState = faker.random().nextBoolean();
        this.progressMs = faker.random().nextLong();
        this.playingType = faker.options().option(PlayingType.class);
        this.user = UserEntityFaker.create().get();

        if (numberOfDevices <= 0) {
            this.devices = DevicesFaker.create().get();
        } else {
            this.devices = DevicesFaker.create(numberOfDevices).get();
        }
        this.playingItem = TrackItemEntity.of(RandomStringUtils.randomAlphanumeric(16));
    }

    public static PlayerStateFaker create() {
        return new PlayerStateFaker();
    }

    public static PlayerStateFaker createWithCustomNumberOfDevices(int deviceNumber) {
        return new PlayerStateFaker(deviceNumber);
    }

    public PlayerStateFaker setPlayableItem(Function<PlayableItemEntity, PlayableItemEntity> modifier) {
        this.playingItem = modifier.apply(playingItem);
        return this;
    }

    public PlayerState get() {
        return asInMemoryPlayerState();
    }

    public PersistablePlayerState asPersistablePlayerState() {
        return PersistablePlayerState.builder()
                .id(id)
                .repeatState(repeatState)
                .shuffleState(shuffleState)
                .devices(devices)
                .playing(playing)
                .playingType(playingType)
                .progressMs(progressMs)
                .user(user)
                .currentlyPlayingItem(playingItem)
                .build();
    }

    public InMemoryPlayerState asInMemoryPlayerState() {
        return InMemoryPlayerState.builder()
                .id(id)
                .repeatState(repeatState)
                .shuffleState(shuffleState)
                .devices(devices)
                .playing(playing)
                .playingType(playingType)
                .progressMs(progressMs)
                .user(user)
                .currentlyPlayingItem(playingItem)
                .build();
    }
}
