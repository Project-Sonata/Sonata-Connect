package testing;

import com.odeyalo.sonata.connect.entity.InMemoryPlayerState;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.repository.PlayerStatePersistentOperations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerStatePersistentOperationsTestAdapter {

    final PlayerStatePersistentOperations<PlayerState> testTarget;
    final TestEntityGenerator<? extends PlayerState> entityGenerator;

    protected PlayerState entity;

    public PlayerStatePersistentOperationsTestAdapter(PlayerStatePersistentOperations<? extends PlayerState> testTarget,
                                                      TestEntityGenerator<? extends PlayerState> entityGenerator) {
        this.testTarget = (PlayerStatePersistentOperations<PlayerState>) testTarget;
        this.entityGenerator = entityGenerator;
    }

    @BeforeAll
    void prepare() {
        PlayerState playerState = entityGenerator.generateValidEntity();
        this.entity = testTarget.save(playerState).block();

    }

    @Test
    public void shouldSave() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result).isNotNull();
    }

    @Test
    public void shouldNotSave() {
        PlayerState entity = entityGenerator.generateInvalidEntity();
        assertThatThrownBy(() -> testTarget.save(entity).block())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldRetrieveEntityById() {
        PlayerState state = testTarget.findById(entity.getId()).block();
        assertThat(state)
                .as("Player state must be found")
                .isNotNull()
                .isEqualTo(entity);
    }

    @Test
    public void shouldNullForNotExistingId() {
        PlayerState state = testTarget.findById(-1L).block();
        assertThat(state)
                .as("Player state must be null if ID does not exist")
                .isNull();
    }

    @Test
    public void shouldDeleteById() {
        testTarget.deleteById(entity.getId()).block();

        PlayerState afterDeletion = testTarget.findById(entity.getId()).block();

        assertThat(afterDeletion)
                .as("After deletion player should be deleted")
                .isNull();
    }

    @Test
    public void shouldNotDeleteByNotExistingId() {
        Long beforeDeletion = testTarget.count().block();
        testTarget.deleteById(-1L).block();
        Long afterDeletion = testTarget.count().block();

        assertThat(afterDeletion)
                .as("The size should not be changed, if ID does not exist")
                .isEqualTo(beforeDeletion);
    }
}