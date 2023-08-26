package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.PlayingType;
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

    final Faker faker = Faker.instance();

    public PlayableItemFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(16);
    }

//    public static PlayableItemFaker forType(PlayingType playingType) {
//        if (playingType == PlayingType.TRACK) {
//            return
//        }
//    }

}
