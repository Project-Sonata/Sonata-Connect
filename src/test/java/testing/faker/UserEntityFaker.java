package testing.faker;

import com.odeyalo.sonata.connect.entity.UserEntity;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

@Setter
@Accessors(chain = true)
public class UserEntityFaker {
    String id;

    public UserEntityFaker() {
        this.id = RandomStringUtils.random(15);
    }

    public static UserEntityFaker create() {
        return new UserEntityFaker();
    }

    public UserEntity get() {
        return buildInMemoryUser();
    }

    public UserEntity asInMemoryUser() {
        return buildInMemoryUser();
    }

    private UserEntity buildInMemoryUser() {
        return UserEntity.builder()
                .id(id)
                .build();
    }
}
