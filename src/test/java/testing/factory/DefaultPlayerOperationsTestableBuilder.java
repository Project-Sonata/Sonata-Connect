package testing.factory;

import com.odeyalo.sonata.connect.config.Converters;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.DefaultPlayerOperations;
import com.odeyalo.sonata.connect.service.player.DeviceOperations;
import com.odeyalo.sonata.connect.service.player.handler.PauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.PredefinedPlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.validation.HardCodedPauseCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.player.support.validation.HardcodedPlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import testing.stub.NullDeviceOperations;

import java.util.ArrayList;
import java.util.List;

public final class DefaultPlayerOperationsTestableBuilder {
    private static final PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();
    private static final DeviceOperations deviceOperations = new NullDeviceOperations();

    private static final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport = new Converters().playerState2CurrentPlayerStateConverter();
    private static final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter = new Converters().currentPlayerStateConverter();

    private final PauseCommandHandlerDelegate pauseCommandHandlerDelegate =
            new PlayerStateUpdatePauseCommandHandlerDelegate(playerStateRepository,
                    new HardCodedPauseCommandPreExecutingIntegrityValidator(),
                    playerStateConverterSupport);
    public static DefaultPlayerOperationsTestableBuilder testableBuilder() {
        return new DefaultPlayerOperationsTestableBuilder();
    }

    private final List<PlayableItem> existingItems = new ArrayList<>();

    public DefaultPlayerOperationsTestableBuilder withState(PlayerStateEntity state) {
        playerStateRepository.save(state).block();
        return this;
    }

    public DefaultPlayerOperationsTestableBuilder withPlayableItems(PlayableItem... items) {
        existingItems.addAll(List.of(items));
        return this;
    }

    public DefaultPlayerOperations build() {
        return new DefaultPlayerOperations(
                playerStateRepository,
                deviceOperations, playerStateConverterSupport,
                PlayCommandHandlerBuilder.builder().withPlayableItems(existingItems).build(),
                playerStateConverter,
                pauseCommandHandlerDelegate);
    }

    static final class PlayCommandHandlerBuilder {
        private PlayableItemLoader itemLoader = new PredefinedPlayableItemLoader();

        public static PlayCommandHandlerBuilder builder() {
            return new PlayCommandHandlerBuilder();
        }

        @NotNull
        public PlayCommandHandlerBuilder withPlayableItems(final List<PlayableItem> existingItems) {
            itemLoader = new PredefinedPlayableItemLoader(existingItems);
            return this;
        }

        public PlayCommandHandlerDelegate build() {
            return new PlayerStateUpdatePlayCommandHandlerDelegate(
                    playerStateRepository,
                    playerStateConverterSupport,
                    itemLoader,
                    new HardcodedPlayCommandPreExecutingIntegrityValidator()
            );
        }
    }
}
