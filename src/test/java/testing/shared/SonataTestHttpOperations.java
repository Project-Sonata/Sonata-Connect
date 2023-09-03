package testing.shared;

import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;

/**
 * Provide all up-to-update http endpoints that can be done for this microservice.
 * if anything was changed, then this interface should be updated.
 *
 * Note that this is blocking impl that used only for tests only
 */
public interface SonataTestHttpOperations {

    PlayerStateDto getCurrentState(String authorizationHeaderValue);

    AvailableDevicesResponseDto getConnectedDevices(String authorizationHeaderValue);

    void connectDevice(String authorizationHeaderValue, ConnectDeviceRequest body);

    void playOrResumePlayback(String authorizationHeaderValue, PlayResumePlaybackRequest body);

    void changeShuffle(String authorizationHeaderValue, boolean shuffleMode);
}
