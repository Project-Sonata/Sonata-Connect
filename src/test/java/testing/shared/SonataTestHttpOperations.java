package testing.shared;

import com.odeyalo.sonata.connect.dto.*;

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

    void switchDevices(String authorizationHeaderValue, DeviceSwitchRequest body);

    SCATokenExchangeResponseDto exchangeScat(String scat);
}
