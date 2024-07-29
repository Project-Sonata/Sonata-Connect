package testing.spring.callback;

import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public final class ClearPlayerStateListener implements TestExecutionListener {
    private final Logger logger = LoggerFactory.getLogger(ClearPlayerStateListener.class);

    @Override
    public void afterTestMethod(@NotNull final TestContext testContext) {
        try {
            final PlayerStateRepository playerStateRepository = testContext.getApplicationContext().getBean(PlayerStateRepository.class);

            playerStateRepository.clear().block();
        } catch (final BeansException ex) {
            logger.warn("No PlayerStateRepository bean found while running the test: {}", testContext.getTestMethod());
        }
    }
}
