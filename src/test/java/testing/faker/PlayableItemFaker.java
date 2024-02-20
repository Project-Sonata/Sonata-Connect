package testing.faker;

import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.TrackItem;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayableItemFaker {
    protected String id;

    public PlayableItemFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(16);
    }

    public static PlayableItemFaker create() {
        return new PlayableItemFaker();
    }

    public PlayableItem get() {
        return TrackItem.of(id);
    }
}
