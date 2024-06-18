package testing.factory;

import com.odeyalo.sonata.connect.config.Converters;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.DefaultPlayerOperations;
import com.odeyalo.sonata.connect.service.player.DeviceOperations;
import com.odeyalo.sonata.connect.service.player.handler.PauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.support.HardcodedPlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.validation.HardCodedPauseCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.player.support.validation.HardcodedPlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import testing.stub.NullDeviceOperations;

public final class DefaultPlayerOperationsTestableBuilder {
    private final PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();
    private final DeviceOperations deviceOperations = new NullDeviceOperations();
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport = new Converters().playerState2CurrentPlayerStateConverter();
    private final PlayCommandHandlerDelegate playCommandHandlerDelegate = new PlayerStateUpdatePlayCommandHandlerDelegate(playerStateRepository, playerStateConverterSupport,
            new HardcodedPlayableItemLoader(),
            new HardcodedPlayCommandPreExecutingIntegrityValidator());
    private final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter = new Converters().currentPlayerStateConverter();
    private final PauseCommandHandlerDelegate pauseCommandHandlerDelegate =
            new PlayerStateUpdatePauseCommandHandlerDelegate(playerStateRepository, new HardCodedPauseCommandPreExecutingIntegrityValidator(), playerStateConverterSupport);

    public static DefaultPlayerOperationsTestableBuilder testableBuilder() {
        return new DefaultPlayerOperationsTestableBuilder();
    }

    public DefaultPlayerOperationsTestableBuilder withState(PlayerStateEntity state) {
        playerStateRepository.save(state).block();
        return this;
    }

    public DefaultPlayerOperations build() {
        return new DefaultPlayerOperations(playerStateRepository,
                deviceOperations, playerStateConverterSupport,
                playCommandHandlerDelegate, playerStateConverter,
                pauseCommandHandlerDelegate);
    }
}
