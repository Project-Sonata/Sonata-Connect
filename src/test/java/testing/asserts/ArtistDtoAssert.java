package testing.asserts;

import com.odeyalo.sonata.connect.dto.ArtistDto;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public final class ArtistDtoAssert extends AbstractAssert<ArtistDtoAssert, ArtistDto> {

    public ArtistDtoAssert(final ArtistDto artistDto) {
        super(artistDto, ArtistDtoAssert.class);
    }

    public ArtistDtoAssert hasName(final String expectedName) {
        assertThat(actual.getName()).isEqualTo(expectedName);
        return this;
    }

    public ArtistDtoAssert hasId(final String expectedId) {
        assertThat(actual.getId()).isEqualTo(expectedId);
        return this;
    }

    public ArtistDtoAssert hasContextUri(final String expectedContextUri) {
        assertThat(actual.getContextUri()).isEqualTo(expectedContextUri);
        return this;
    }
}
