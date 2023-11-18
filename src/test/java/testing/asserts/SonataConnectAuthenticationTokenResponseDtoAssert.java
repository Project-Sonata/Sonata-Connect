package testing.asserts;

import com.odeyalo.sonata.connect.dto.SonataConnectAuthenticationTokenResponseDto;
import org.assertj.core.api.AbstractAssert;

public class SonataConnectAuthenticationTokenResponseDtoAssert extends AbstractAssert<SonataConnectAuthenticationTokenResponseDtoAssert, SonataConnectAuthenticationTokenResponseDto> {

    protected SonataConnectAuthenticationTokenResponseDtoAssert(SonataConnectAuthenticationTokenResponseDto deviceRegistrationResponseDto) {
        super(deviceRegistrationResponseDto, SonataConnectAuthenticationTokenResponseDtoAssert.class);
    }

    public static SonataConnectAuthenticationTokenResponseDtoAssert forResponseBody(SonataConnectAuthenticationTokenResponseDto actual) {
        return new SonataConnectAuthenticationTokenResponseDtoAssert(actual);
    }

    public SonataConnectAuthenticationTokenResponseDtoAssert hasToken() {
        if ( actual.getToken() == null ) {
            throw failure("Token is null but required to be not null");
        }
        return this;
    }
}
