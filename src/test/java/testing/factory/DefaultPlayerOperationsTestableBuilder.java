package testing.factory;

import com.odeyalo.sonata.connect.config.Converters;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.entity.factory.DefaultPlayerStateEntityFactory;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.DefaultPlayerOperations;
import com.odeyalo.sonata.connect.service.player.PlayerStateService;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.PredefinedPlayableItemLoader;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class DefaultPlayerOperationsTestableBuilder {
    private final PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport = new Converters().playerState2CurrentPlayerStateConverter();
    private final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter = new Converters().currentPlayerStateConverter();

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
                PlayCommandHandlerBuilder.builder()
                        .withState(playerStateRepository)
                        .withPlayableItems(existingItems)
                        .build(),
                playerStateConverter,
                new PlayerStateService(playerStateRepository, playerStateConverterSupport,
                new DefaultPlayerStateEntityFactory(new DeviceEntity.Factory(), new TrackItemEntity.Factory())));
    }

    static final class PlayCommandHandlerBuilder {
        private PlayableItemLoader itemLoader = new PredefinedPlayableItemLoader();
        private PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

        public static PlayCommandHandlerBuilder builder() {
            return new PlayCommandHandlerBuilder();
        }

        @NotNull
        public PlayCommandHandlerBuilder withState(PlayerStateRepository repository) {
            this.playerStateRepository = repository;
            return this;
        }

        @NotNull
        public PlayCommandHandlerBuilder withPlayableItems(final List<PlayableItem> existingItems) {
            itemLoader = new PredefinedPlayableItemLoader(existingItems);
            return this;
        }

        public PlayCommandHandlerDelegate build() {
            return new PlayerStateUpdatePlayCommandHandlerDelegate(
                    itemLoader,
                    new PlayerStateService(playerStateRepository, testableBuilder().playerStateConverterSupport, new DefaultPlayerStateEntityFactory(
                            new DeviceEntity.Factory(), new TrackItemEntity.Factory()
                    )));
        }
    }
}
