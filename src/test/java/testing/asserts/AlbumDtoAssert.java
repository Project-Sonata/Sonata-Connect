package testing.asserts;

import com.odeyalo.sonata.connect.dto.AlbumDto;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public final class AlbumDtoAssert extends AbstractAssert<AlbumDtoAssert, AlbumDto> {

    public AlbumDtoAssert(final AlbumDto actual) {
        super(actual, AlbumDtoAssert.class);
    }

    public AlbumDtoAssert hasId(final String id) {
        assertThat(actual.getId()).isEqualTo(id);
        return this;
    }

    public AlbumDtoAssert hasName(final String name) {
        assertThat(actual.getName()).isEqualTo(name);
        return this;
    }

    public AlbumDtoAssert hasContextUri(final String contextUri) {
        assertThat(actual.getContextUri()).isEqualTo(contextUri);
        return this;
    }

    public AlbumDtoAssert hasAlbumType(final AlbumSpec.AlbumType albumType) {
        assertThat(actual.getAlbumType()).isEqualTo(albumType);
        return this;
    }
}