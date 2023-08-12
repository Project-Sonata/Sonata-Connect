package testing;

import com.odeyalo.sonata.connect.entity.PlayerState;

/**
 * Used to generate the test object
 */
public interface TestEntityGenerator<T> {

    T generateValidEntity();

    T generateInvalidEntity();
}
