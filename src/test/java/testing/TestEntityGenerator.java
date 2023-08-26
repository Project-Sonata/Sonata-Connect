package testing;

/**
 * Used to generate the test object
 */
public interface TestEntityGenerator<T> {

    T generateValidEntity();

    T generateInvalidEntity();
}
