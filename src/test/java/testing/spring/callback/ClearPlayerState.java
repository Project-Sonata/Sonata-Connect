package testing.spring.callback;

import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation that used for tests, that allows to clear the player state AFTER each test method.
 * @see ClearPlayerStateListener
 */
@Retention(RUNTIME)
@Target(TYPE)
@TestExecutionListeners(
        listeners = ClearPlayerStateListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public @interface ClearPlayerState {
}
